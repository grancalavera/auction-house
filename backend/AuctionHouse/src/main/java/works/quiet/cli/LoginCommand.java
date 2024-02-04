package works.quiet.cli;

import lombok.extern.java.Log;
import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;
import java.util.logging.Level;

@Log
@CommandLine.Command(
        name = "login",
        description = "Login with username and password, and persists an user session.",
        mixinStandardHelpOptions = true
)
public class LoginCommand implements Callable<Integer> {
    private final AdminService adminService;
    @CommandLine.Option(names = {"-u", "--username"}, required = true)
    private String username;
    @CommandLine.Option(names = {"-p", "--password"}, required = true)
    private String password;

    public LoginCommand(Level logLevel, AdminService adminService) {
        this.adminService = adminService;
        log.setLevel(logLevel);
    }

    @Override
    public Integer call() throws Exception {
        try {
            adminService.login(username, password);
            adminService.assertIsNotBlocked();
            System.out.printf("Logged in as '%s'.\n", username);
        } catch (Exception e) {
            adminService.logout();
            throw e;
        }
        return 0;
    }
}
