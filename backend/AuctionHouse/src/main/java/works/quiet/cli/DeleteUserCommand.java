package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "delete-user",
        description = "Deletes an existing user.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class DeleteUserCommand implements Callable<Integer> {
    private final AdminService adminService;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(
            names = {"-f", "--force"},
            description = "Force confirmation."
    )
    private boolean force;

    @CommandLine.Parameters(paramLabel = "USER_ID", description = "The user id to delete.")
    private int userId;

    public DeleteUserCommand(final AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Integer call() throws Exception {

        if (!force) {
            spec.commandLine().getErr().printf(
                    "Do you really want to delete user.id=%d? If so, retry with --force to continue.",
                    userId
            );
            return 1;
        }

        adminService.assertIsNotBlocked();
        adminService.assertIsAdmin();
        adminService.deleteUserById(userId);

        spec.commandLine().getOut().printf("Deleted user with user.id=%d.\n", userId);
        return 0;
    }
}
