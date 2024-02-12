package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;
import java.util.logging.Level;

@CommandLine.Command(
        name = "unblock-user",
        description = "Unlock users by id.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class UnblockUserCommand extends CommandWithAdmin {

    @CommandLine.Parameters(paramLabel = "USER_ID", description = "The user id to unblock.")
    private int userId;

    public UnblockUserCommand(final Level logLevel, final Resources resources, final AdminService adminService) {
        super(logLevel, resources, adminService);
    }

    @Override
    public Integer call() throws Exception {
        adminService.assertIsNotBlocked();
        adminService.assertIsAdmin();
        adminService.unblockUser(userId);
        System.out.printf("Unblocked user with user.id=%d.\n", userId);
        return 0;
    }
}
