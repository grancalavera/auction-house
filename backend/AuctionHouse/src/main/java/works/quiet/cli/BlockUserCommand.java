package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;
import java.util.logging.Level;

@CommandLine.Command(
        name = "block-user",
        description = "Block users by id.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class BlockUserCommand implements Callable<Integer> {

    private final AdminService adminService;

    @CommandLine.Parameters(paramLabel = "USER_ID", description = "The user id to block.")
    private int userId;

    public BlockUserCommand(Level logLevel, AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Integer call() throws Exception {
        adminService.blockUser(userId);
        System.out.printf("Blocked user with user.id=%d.\n", userId);
        return 0;
    }
}
