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

@Log
class AuctionHouse {
    public static void main(String... argv) {

        DBConnection connection = new PGConnection(
                System.getenv("AH_DB_URL"),
                System.getenv("AH_DB_USER"),
                System.getenv("AH_DB_PASSWORD"));

        OrganisationRepository organisationRepository = new PGOrganisationRepository(connection);
        Map<Integer, OrganisationModel> organisations = organisationRepository.getAll();

        UserRepository userRepository = new PGUserRepository(connection, organisations);
        Session session = new FileSystemSession();
        AdminService adminService = new AdminService(userRepository, session);

        CommandLine mainProgram = new CommandLine(new MainProgram());

        CommandLine adminProgram = new CommandLine(new AdminProgram());
        adminProgram.addSubcommand("create-user", new CreateUserCommand());
        adminProgram.addSubcommand("help", new CommandLine.HelpCommand());

        mainProgram.addSubcommand("login", new LoginCommand(adminService));
        mainProgram.addSubcommand("logout", new LogoutCommand(adminService));
        mainProgram.addSubcommand("admin", adminProgram);
        mainProgram.addSubcommand("help", new CommandLine.HelpCommand());

        int exitCode = mainProgram.execute(argv);

        try {
            connection.close();
        } catch (Exception ex) {
            log.warning("Failed to close DBConnection");
        }

        System.exit(exitCode);
    }
}