package works.quiet;

import lombok.extern.java.Log;
import picocli.CommandLine;
import works.quiet.cli.*;
import works.quiet.dao.PGDaoMapper;
import works.quiet.dao.PGUserDaoMapper;
import works.quiet.dao.PGUserDao;
import works.quiet.io.DBConnection;
import works.quiet.io.PGConnection;
import works.quiet.reference.OrganisationRepository;
import works.quiet.reference.PGOrganisationRepository;
import works.quiet.user.*;

import java.util.logging.Level;

@Log
class AuctionHouse {
    private final static Level DEFAULT_LOG_LEVEL = Level.OFF;

    public static void main(String... argv) {
        var AH_DB_URL = System.getenv("AH_DB_URL");
        var AH_DB_USER = System.getenv("AH_DB_USER");
        var AH_DB_PASSWORD = System.getenv("AH_DB_PASSWORD");
        var AH_LOG_LEVEL = System.getenv("AH_LOG_LEVEL");

        Level LOG_LEVEL;

        try {
            LOG_LEVEL = Level.parse(AH_LOG_LEVEL);
        } catch (IllegalArgumentException e) {
            log.setLevel(Level.WARNING);
            log.warning("Failed to parse log level from AH_LOG_LEVEL, defaulting to \"" + DEFAULT_LOG_LEVEL + "\".");
            LOG_LEVEL = DEFAULT_LOG_LEVEL;
        }

        DBConnection connection = new PGConnection(
                LOG_LEVEL,
                AH_DB_URL,
                AH_DB_USER,
                AH_DB_PASSWORD
        );

        AdminService adminService = getAdminService(LOG_LEVEL, connection);

        CommandLine mainProgram = new CommandLine(new MainProgram());

        CommandLine adminProgram = new CommandLine(new AdminProgram());
        adminProgram.addSubcommand("list-users", new ListUsersCommand(adminService));
        adminProgram.addSubcommand("create-user", new CreateUserCommand(adminService));
        adminProgram.addSubcommand("list-organisations", new ListOrganisationsCommand(adminService));
        adminProgram.addSubcommand("block-user", new BlockUserCommand(LOG_LEVEL, adminService));
        adminProgram.addSubcommand("unblock-user", new UnblockUserCommand(LOG_LEVEL, adminService));
        adminProgram.addSubcommand("help", new CommandLine.HelpCommand());

        mainProgram.addSubcommand("login", new LoginCommand(LOG_LEVEL, adminService));
        mainProgram.addSubcommand("logout", new LogoutCommand(adminService));
        mainProgram.addSubcommand("whoami", new WhoAmICommand(LOG_LEVEL, adminService));
        mainProgram.addSubcommand("admin", adminProgram);
        mainProgram.addSubcommand("boom", new BoomCommand());
        mainProgram.addSubcommand("show-config", new ShowConfigCommand(AH_DB_URL, AH_DB_USER, LOG_LEVEL));
        mainProgram.addSubcommand("help", new CommandLine.HelpCommand());


        int exitCode = mainProgram
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute(argv);

        try {
            connection.close();
        } catch (Exception ex) {
            log.warning("Failed to close DBConnection.");
        }

        System.exit(exitCode);
    }

    private static AdminService getAdminService(Level LOG_LEVEL, DBConnection connection) {
        OrganisationRepository organisationRepository = new PGOrganisationRepository(LOG_LEVEL, connection);
        Session session = new FileSystemSession(LOG_LEVEL);
        PGDaoMapper<UserModel> userMapper = new PGUserDaoMapper(LOG_LEVEL);
        PGUserDao userDao = new PGUserDao(LOG_LEVEL, connection, userMapper);
        UserRepository userRepository = new PGUserRepository(LOG_LEVEL, userDao, connection);
        UserValidator userValidator = new UserValidator(LOG_LEVEL);
        return new AdminService(LOG_LEVEL, userRepository, organisationRepository, session, userValidator);
    }
}