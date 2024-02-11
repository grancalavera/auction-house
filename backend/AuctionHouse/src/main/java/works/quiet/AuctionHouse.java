package works.quiet;

import lombok.extern.java.Log;
import picocli.CommandLine;
import works.quiet.cli.AdminProgram;
import works.quiet.cli.BlockUserCommand;
import works.quiet.cli.BoomCommand;
import works.quiet.cli.CheckUserExistsCommand;
import works.quiet.cli.CountUsersCommand;
import works.quiet.cli.CreateUserCommand;
import works.quiet.cli.DeleteUserCommand;
import works.quiet.cli.FindUserCommand;
import works.quiet.cli.ListOrganisationsCommand;
import works.quiet.cli.ListUsersCommand;
import works.quiet.cli.LoginCommand;
import works.quiet.cli.LogoutCommand;
import works.quiet.cli.MainProgram;
import works.quiet.cli.PrintExceptionMessageHandler;
import works.quiet.cli.ShowConfigCommand;
import works.quiet.cli.UnblockUserCommand;
import works.quiet.cli.UpdateUserCommand;
import works.quiet.cli.WhoAmICommand;
import works.quiet.db.DBConnection;
import works.quiet.db.PGConnection;
import works.quiet.db.PGMapper;
import works.quiet.db.RepositoryQuery;
import works.quiet.reference.Organisation;
import works.quiet.reference.OrganisationRepository;
import works.quiet.reference.PGOrganisationMapper;
import works.quiet.reference.PGOrganisationRepository;
import works.quiet.reference.PGOrganisationRepositoryQuery;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;
import works.quiet.user.FileSystemSession;
import works.quiet.user.PGUserMapper;
import works.quiet.user.PGUserRepository;
import works.quiet.user.PGUserRepositoryQuery;
import works.quiet.user.Session;
import works.quiet.user.User;
import works.quiet.user.UserRepository;
import works.quiet.user.UserValidator;

import java.util.ResourceBundle;
import java.util.logging.Level;

@Log
class AuctionHouse {
    private static final Level DEFAULT_LOG_LEVEL = Level.OFF;


    public static void main(final String... argv) {
        var ahDbUrl = System.getenv("AH_DB_URL");
        var ahDbUser = System.getenv("AH_DB_USER");
        var ahDbPassword = System.getenv("AH_DB_PASSWORD");
        var ahLogLevel = System.getenv("AH_LOG_LEVEL");

        var bundle = ResourceBundle.getBundle("strings");
        var badLogin = bundle.getString("errors.badLogin");

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


        AdminService adminService = getAdminService(logLevel, connection);
        Resources resources = new Resources();

        CommandLine mainProgram = new CommandLine(new MainProgram());

        CommandLine adminProgram = new CommandLine(new AdminProgram());
        adminProgram.addSubcommand("find-user", new FindUserCommand(adminService));
        adminProgram.addSubcommand("list-users", new ListUsersCommand(adminService));
        adminProgram.addSubcommand("count-users", new CountUsersCommand(adminService));
        adminProgram.addSubcommand("check-user-exists", new CheckUserExistsCommand(adminService));
        adminProgram.addSubcommand("create-user", new CreateUserCommand(adminService));
        adminProgram.addSubcommand("update-user", new UpdateUserCommand(adminService));
        adminProgram.addSubcommand("delete-user", new DeleteUserCommand(adminService));
        adminProgram.addSubcommand("list-organisations", new ListOrganisationsCommand(adminService));
        adminProgram.addSubcommand("block-user", new BlockUserCommand(logLevel, adminService));
        adminProgram.addSubcommand("unblock-user", new UnblockUserCommand(logLevel, adminService));
        adminProgram.addSubcommand("help", new CommandLine.HelpCommand());

        mainProgram.addSubcommand("login", new LoginCommand(resources, adminService));
        mainProgram.addSubcommand("logout", new LogoutCommand(adminService));
        mainProgram.addSubcommand("whoami", new WhoAmICommand(logLevel, adminService));
        mainProgram.addSubcommand("admin", adminProgram);
        mainProgram.addSubcommand("boom", new BoomCommand());
        mainProgram.addSubcommand("show-config", new ShowConfigCommand(ahDbUrl, ahDbUser, logLevel));
        mainProgram.addSubcommand("help", new CommandLine.HelpCommand());

        int exitCode = mainProgram
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute(argv);

        try {
            connection.close();
        } catch (final Exception ex) {
            log.warning("Failed to close DBConnection.");
        }

        System.exit(exitCode);
    }

    private static AdminService getAdminService(final Level logLevel, final DBConnection connection) {

        Session session = new FileSystemSession(logLevel);
        PGUserRepositoryQuery userRepoQuery = new PGUserRepositoryQuery(logLevel, connection);
        RepositoryQuery<Organisation> organisationRepoQuery = new PGOrganisationRepositoryQuery(logLevel, connection);
        PGMapper<Organisation> orgMapper = new PGOrganisationMapper();
        OrganisationRepository organisationRepository = new PGOrganisationRepository(
                logLevel, organisationRepoQuery, orgMapper);

        PGMapper<User> userMapper = new PGUserMapper(logLevel);
        UserRepository userRepository = new PGUserRepository(logLevel, userRepoQuery, connection, userMapper);
        UserValidator userValidator = new UserValidator(logLevel);

        return new AdminService(logLevel, userRepository, organisationRepository, session, userValidator);
    }
}
