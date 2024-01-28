package works.quiet;

import lombok.extern.java.Log;
import picocli.CommandLine;
import works.quiet.cli.*;
import works.quiet.io.DBConnection;
import works.quiet.io.PGConnection;
import works.quiet.reference.OrganisationModel;
import works.quiet.reference.OrganisationRepository;
import works.quiet.reference.PGOrganisationRepository;
import works.quiet.user.*;

import java.util.Map;
import java.util.logging.Level;

@Log
class AuctionHouse {
    public static void main(String... argv) {

        var LOG_LEVEL = Level.OFF;

        DBConnection connection = new PGConnection(
                System.getenv("AH_DB_URL"),
                System.getenv("AH_DB_USER"),
                System.getenv("AH_DB_PASSWORD"));

        AdminService adminService = getAdminService(LOG_LEVEL, connection);

        CommandLine mainProgram = new CommandLine(new MainProgram());

        CommandLine adminProgram = new CommandLine(new AdminProgram());
        adminProgram.addSubcommand("list-users", new ListUsersCommand(adminService));
        adminProgram.addSubcommand("create-user", new CreateUserCommand(adminService));
        adminProgram.addSubcommand("help", new CommandLine.HelpCommand());

        mainProgram.addSubcommand("login", new LoginCommand(LOG_LEVEL, adminService));
        mainProgram.addSubcommand("logout", new LogoutCommand(adminService));
        mainProgram.addSubcommand("whoami", new WhoAmICommand(LOG_LEVEL, adminService));
        mainProgram.addSubcommand("admin", adminProgram);
        mainProgram.addSubcommand("help", new CommandLine.HelpCommand());

        int exitCode = mainProgram.execute(argv);

        try {
            connection.close();
        } catch (Exception ex) {
            log.warning("Failed to close DBConnection.");
        }

        System.exit(exitCode);
    }

    private static AdminService getAdminService(Level LOG_LEVEL, DBConnection connection) {
        OrganisationRepository organisationRepository = new PGOrganisationRepository(LOG_LEVEL, connection);
        Map<Integer, OrganisationModel> organisations = organisationRepository.getAll();

        UserRepository userRepository = new PGUserRepository(LOG_LEVEL, connection, organisations);
        Session session = new FileSystemSession(LOG_LEVEL);
        UserValidator userValidator = new UserValidator(LOG_LEVEL);

        return new AdminService(LOG_LEVEL, userRepository, session, userValidator);
    }
}