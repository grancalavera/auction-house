package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;

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

    public LoginCommand(final AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Integer call() throws Exception {
        adminService.login(username, password);
        adminService.assertIsNotBlocked();
        System.out.printf("Logged in as '%s'.\n", username);
        return 0;
    }
}
