package works.quiet.cli;

import lombok.extern.java.Log;
import picocli.CommandLine;
import works.quiet.user.AdminService;
import works.quiet.user.BadLoginException;

import java.util.concurrent.Callable;
import java.util.logging.Level;

@Log
@CommandLine.Command(
        name = "login",
        description = "Login with username and password, and persists an user session.",
        mixinStandardHelpOptions = true
)
public class LoginCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-u", "--username"}, required = true)
    private String username;

    @CommandLine.Option(names = {"-p", "--password"}, required = true)
    private String password;

    private final AdminService adminService;

    public LoginCommand(Level logLevel, AdminService adminService) {
        this.adminService = adminService;
        log.setLevel(logLevel);
    }

    @Override
    public Integer call() {
        var exitCode = 1;

        try {
            adminService.login(username, password);
            System.out.printf("Logged in as '%s'.\n", username);
            exitCode = 0;
        } catch (BadLoginException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            log.severe(ex.getMessage());
            System.out.println("Unknown error.");
        }

        return exitCode;
    }
}
