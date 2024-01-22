package works.quiet;

import lombok.extern.java.Log;
import picocli.CommandLine;
import works.quiet.admin.AdminService;
import works.quiet.cli.AdminProgram;
import works.quiet.cli.MainProgram;
import works.quiet.cli.CreateUserCommand;
import works.quiet.cli.LoginCommand;
import works.quiet.cli.LogoutCommand;
import works.quiet.io.DBConnection;
import works.quiet.io.PGConnection;
import works.quiet.user.PGUserDao;
import works.quiet.user.UserDao;

@Log
class AuctionHouse {
    public static void main(String ... argv) {
        DBConnection connection = new PGConnection("jdbc:postgresql://localhost:5432/auction-house","grancalavera");
        UserDao userDao = new PGUserDao(connection);
        AdminService adminService = new AdminService(userDao);

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
        }catch(Exception ex) {
            log.warning("Failed to close DBConnection");
        }

        System.exit(exitCode);
    }
}