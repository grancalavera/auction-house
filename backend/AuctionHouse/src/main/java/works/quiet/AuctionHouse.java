package works.quiet;

import lombok.extern.java.Log;
import picocli.CommandLine;
import works.quiet.auction.AuctionService;
import works.quiet.auction.PGAuctionIdSource;
import works.quiet.auction.PGAuctionRawQueryMapper;
import works.quiet.auction.PGAuctionRepository;
import works.quiet.auction.PGAuctionRowMapper;
import works.quiet.auction.PGBidIdSource;
import works.quiet.auction.PGBidRepository;
import works.quiet.auction.PGBidRowMapper;
import works.quiet.cli.AdminCommand;
import works.quiet.cli.AuctionCommand;
import works.quiet.cli.BlockUserCommand;
import works.quiet.cli.BoomCommand;
import works.quiet.cli.CheckUserExistsCommand;
import works.quiet.cli.CloseAuctionCommand;
import works.quiet.cli.CountUsersCommand;
import works.quiet.cli.CreateAuctionCommand;
import works.quiet.cli.CreateUserCommand;
import works.quiet.cli.DeleteUserCommand;
import works.quiet.cli.FindUserCommand;
import works.quiet.cli.ListMyAuctionsCommand;
import works.quiet.cli.ListOpenAuctionsCommand;
import works.quiet.cli.ListOrganisationsCommand;
import works.quiet.cli.ListUsersCommand;
import works.quiet.cli.LoginCommand;
import works.quiet.cli.LogoutCommand;
import works.quiet.cli.MainCommand;
import works.quiet.cli.PlaceBidCommand;
import works.quiet.cli.PrintExceptionMessageHandler;
import works.quiet.cli.ShowConfigCommand;
import works.quiet.cli.UnblockUserCommand;
import works.quiet.cli.UpdateUserCommand;
import works.quiet.cli.WhoAmICommand;
import works.quiet.db.DBConnection;
import works.quiet.db.DBInterface;
import works.quiet.db.PGConnection;
import works.quiet.db.PGDBInterface;
import works.quiet.db.PGUpsertMapper;
import works.quiet.reference.PGOrganisationMapper;
import works.quiet.reference.PGOrganisationRepository;
import works.quiet.reports.ExecutionRepository;
import works.quiet.reports.PGReportIdSource;
import works.quiet.reports.PGReportRepository;
import works.quiet.reports.ReportsService;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;
import works.quiet.user.FileSystemSession;
import works.quiet.user.PGUserIdSource;
import works.quiet.user.PGUserMapper;
import works.quiet.user.PGUserRepository;
import works.quiet.user.UserValidator;

import java.util.logging.Level;

@Log
class AuctionHouse {
    private static final Level DEFAULT_LOG_LEVEL = Level.OFF;

    public static void main(final String... argv) {
        var ahDbUrl = System.getenv("AH_DB_URL");
        var ahDbUser = System.getenv("AH_DB_USER");
        var ahDbPassword = System.getenv("AH_DB_PASSWORD");
        var ahLogLevel = System.getenv("AH_LOG_LEVEL");

        Level logLevel;

        try {
            logLevel = Level.parse(ahLogLevel);
        } catch (final IllegalArgumentException e) {
            log.setLevel(Level.WARNING);
            log.warning("Failed to parse log level from AH_LOG_LEVEL, defaulting to \"" + DEFAULT_LOG_LEVEL + "\".");
            logLevel = DEFAULT_LOG_LEVEL;
        }

        DBConnection connection = new PGConnection(
                logLevel,
                ahDbUrl,
                ahDbUser,
                ahDbPassword
        );

        var dbInterface = new PGDBInterface(logLevel, connection);

        var resources = new Resources();
        var adminService = getAdminService(logLevel, resources, dbInterface);
        var auctionService = getAuctionService(logLevel, resources, dbInterface);
        var reportIdSource = new PGReportIdSource(logLevel, dbInterface);
        var reportRepository = new PGReportRepository(
                logLevel,
                resources,
                dbInterface,
                new PGUpsertMapper(logLevel),
                reportIdSource
        );
        var reportsService = new ReportsService(
                logLevel,
                resources,
                dbInterface,
                reportRepository,
                new ExecutionRepository(logLevel, resources, dbInterface)
        );

        // main command
        CommandLine mainCommand = new CommandLine(new MainCommand());
        mainCommand.addSubcommand("login", new LoginCommand(logLevel, resources, adminService));
        mainCommand.addSubcommand("logout", new LogoutCommand(logLevel, resources, adminService));
        mainCommand.addSubcommand("whoami", new WhoAmICommand(logLevel, adminService));
        mainCommand.addSubcommand("help", new CommandLine.HelpCommand());

        // admin command
        CommandLine adminCommand = new CommandLine(new AdminCommand());
        adminCommand.addSubcommand("find-user", new FindUserCommand(logLevel, resources, adminService));
        adminCommand.addSubcommand("list-users", new ListUsersCommand(logLevel, resources, adminService));
        adminCommand.addSubcommand("count-users", new CountUsersCommand(logLevel, resources, adminService));
        adminCommand.addSubcommand("check-user-exists", new CheckUserExistsCommand(logLevel, resources, adminService));
        adminCommand.addSubcommand("create-user", new CreateUserCommand(logLevel, resources, adminService));
        adminCommand.addSubcommand("update-user", new UpdateUserCommand(logLevel, resources, adminService));
        adminCommand.addSubcommand("delete-user", new DeleteUserCommand(logLevel, resources, adminService));
        adminCommand.addSubcommand("list-organisations", new ListOrganisationsCommand(
                logLevel, resources, adminService));
        adminCommand.addSubcommand("block-user", new BlockUserCommand(logLevel, resources, adminService));
        adminCommand.addSubcommand("unblock-user", new UnblockUserCommand(logLevel, resources, adminService));
        adminCommand.addSubcommand("help", new CommandLine.HelpCommand());

        // auction command
        CommandLine auctionCommand = new CommandLine(new AuctionCommand());
        auctionCommand.addSubcommand("create",
                new CreateAuctionCommand(logLevel, resources, adminService, auctionService));
        auctionCommand.addSubcommand("list-mine",
                new ListMyAuctionsCommand(logLevel, resources, adminService, auctionService));
        auctionCommand.addSubcommand("list-open",
                new ListOpenAuctionsCommand(logLevel, resources, adminService, auctionService));
        auctionCommand.addSubcommand("close",
                new CloseAuctionCommand(logLevel, resources, adminService, auctionService, reportsService));
        auctionCommand.addSubcommand("bid",
                new PlaceBidCommand(logLevel, resources, adminService, auctionService));
        auctionCommand.addSubcommand("help", new CommandLine.HelpCommand());

        // hidden commands
        mainCommand.addSubcommand("boom", new BoomCommand());
        mainCommand.addSubcommand("show-config", new ShowConfigCommand(ahDbUrl, ahDbUser, logLevel));

        // sub commands
        mainCommand.addSubcommand("admin", adminCommand);
        mainCommand.addSubcommand("auction", auctionCommand);

        int exitCode = mainCommand
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute(argv);

        try {
            connection.close();
        } catch (final Exception ex) {
            log.warning("Failed to close DBConnection.");
        }

        System.exit(exitCode);
    }

    private static AdminService getAdminService(
            final Level logLevel,
            final Resources resources,
            final DBInterface dbInterface
    ) {

        var organisationRepository = new PGOrganisationRepository(
                logLevel,
                dbInterface,
                new PGOrganisationMapper(logLevel),
                new PGOrganisationIdSource(logLevel, dbInterface)
        );

        var userRepository = new PGUserRepository(
                logLevel,
                dbInterface,
                new PGUserMapper(logLevel),
                new PGUpsertMapper(logLevel),
                new PGUserIdSource(logLevel, dbInterface)
        );

        return new AdminService(
                logLevel,
                resources,
                userRepository,
                organisationRepository,
                new FileSystemSession(logLevel, resources),
                new UserValidator(logLevel, resources)
        );
    }

    private static AuctionService getAuctionService(
            final Level logLevel,
            final Resources resources,
            final DBInterface dbInterface
    ) {

        var upsertMapper = new PGUpsertMapper(logLevel);

        var auctionRepository = new PGAuctionRepository(
                logLevel,
                dbInterface,
                new PGAuctionRawQueryMapper(logLevel, new PGAuctionRowMapper(logLevel), new PGBidRowMapper(logLevel)),
                upsertMapper,
                new PGAuctionIdSource(logLevel, dbInterface)
        );

        var bidRepository = new PGBidRepository(
                logLevel,
                dbInterface,
                upsertMapper,
                new PGBidIdSource(logLevel, dbInterface)
        );

        return new AuctionService(logLevel, resources, auctionRepository, bidRepository);
    }
}
