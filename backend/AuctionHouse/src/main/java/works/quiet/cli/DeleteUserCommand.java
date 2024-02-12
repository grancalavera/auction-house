package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "delete-user",
        description = "Deletes an existing user.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class DeleteUserCommand extends CommandWithAdmin {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(
            names = {"-f", "--force"},
            description = "Force confirmation."
    )
    private boolean force;

    @CommandLine.Parameters(paramLabel = "USER_ID", description = "The user id to delete.")
    private int userId;

    public DeleteUserCommand(final Level logLevel, final Resources resources, final AdminService adminService) {
        super(logLevel, resources, adminService);
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
