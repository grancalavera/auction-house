package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;
import works.quiet.user.UserModel;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "list-users",
        description = "List all existing users.",
        mixinStandardHelpOptions = true
)
public class ListUsersCommand implements Callable<Integer> {

    private final AdminService adminService;

    public ListUsersCommand(AdminService adminService) {
        this.adminService = adminService;
    }


    @Override
    public Integer call() throws Exception {
        adminService.assertIsAdmin();
        adminService.listUsers().forEach(System.out::println);
        return 0;
    }
}
