package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;
import works.quiet.user.User;

import java.util.concurrent.Callable;
import java.util.logging.Level;

@CommandLine.Command(
        name = "find-user",
        description = "Find users by id.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class FindUserCommand extends CommandWithAdmin {

    @CommandLine.Parameters(paramLabel = "USER_ID", description = "The user id to find.")
    private int userId;

    public FindUserCommand(final Level logLevel, final Resources resources, final AdminService adminService) {
        super(logLevel, resources, adminService);
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
