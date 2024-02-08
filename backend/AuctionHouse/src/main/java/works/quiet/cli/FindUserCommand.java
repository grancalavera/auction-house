package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;
import works.quiet.user.User;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "find-user",
        description = "Find users by id.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class FindUserCommand implements Callable<Integer> {

    private final AdminService adminService;

    @CommandLine.Parameters(paramLabel = "USER_ID", description = "The user id to find.")
    private int userId;

    public FindUserCommand(final AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Integer call() throws Exception {
        adminService.assertIsNotBlocked();
        adminService.assertIsAdmin();
        User user = adminService.unsafeFindUserById(userId);
        System.out.println(user);
        return 0;
    }
}
