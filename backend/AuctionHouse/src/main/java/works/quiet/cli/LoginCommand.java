package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "login",
        description = "Login with username and password, and persists an user session.",
        mixinStandardHelpOptions = true
)
public class LoginCommand extends CommandWithAdmin {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(names = {"-u", "--username"}, required = true)
    private String username;

    @CommandLine.Option(names = {"-p", "--password"}, required = true)
    private String password;

    public LoginCommand(final Level logLevel, final Resources resources, final AdminService adminService) {
        super(logLevel, resources, adminService);
    }

    @Override
    public Integer call() throws Exception {
        adminService.login(username, password);
        adminService.assertIsNotBlocked();
        spec.commandLine().getOut().printf("Logged in as \"%s\".\n", username);
        return 0;
    }
}
