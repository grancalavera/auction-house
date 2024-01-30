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
        // this should be handled by the top level admin command
        try {
            adminService.assertIsAdmin();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return 1;
        }

        var users = adminService.listUsers();
        for (UserModel user : users) {
            System.out.println(user.toString());
        }
        return 0;
    }
}
