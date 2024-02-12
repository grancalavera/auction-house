package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "block-user",
        description = "Block users by id.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class BlockUserCommand extends CommandWithAdmin {

    @CommandLine.Parameters(paramLabel = "USER_ID", description = "The user id to block.")
    private int userId;

    public BlockUserCommand(final Level logLevel, final Resources resources, final AdminService adminService) {
        super(logLevel, resources, adminService);
    }

    @Override
    public void run()  {
        adminService.assertIsNotBlocked();
        adminService.assertIsAdmin();
        adminService.blockUser(userId);
        System.out.printf("Blocked user with user.id=%d.\n", userId);
    }
}
