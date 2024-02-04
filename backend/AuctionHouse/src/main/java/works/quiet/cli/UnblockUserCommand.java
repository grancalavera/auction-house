package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;
import java.util.logging.Level;

@CommandLine.Command(
        name="unblock-user",
        description = "Unlock users by id.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class UnblockUserCommand implements Callable<Integer> {

    private final AdminService adminService;

    @CommandLine.Parameters(paramLabel = "USER_ID", description = "The user id to unblock.")
    private int userId;

    public UnblockUserCommand(Level logLevel, AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Integer call() throws Exception {
        adminService.unblockUser(userId);
        System.out.printf("Unblocked user with user.id=%d.\n", userId);
        return 0;
    }
}
