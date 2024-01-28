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
        var users = adminService.listUsers();
        for (UserModel user : users) {
            System.out.println(user.toString());
        }
        return 0;
    }
}
