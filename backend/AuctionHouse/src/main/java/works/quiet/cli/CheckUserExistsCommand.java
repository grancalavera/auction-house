package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "check-user-exists",
        description = "Checks whether an user exists or not.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class CheckUserExistsCommand implements Callable<Integer> {
    private final AdminService adminService;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Parameters(paramLabel = "USER_ID", description = "The user id to check.")
    private int userId;

    public CheckUserExistsCommand(final AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Integer call() throws Exception {
        adminService.assertIsNotBlocked();
        adminService.assertIsAdmin();
        var exists = adminService.userExists(userId);
        spec.commandLine().getOut().println(exists);
        return 0;
    }
}
