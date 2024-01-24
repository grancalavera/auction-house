package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "logout",
        description = "Terminates the current user's session.",
        mixinStandardHelpOptions = true
)
public class LogoutCommand implements Callable<Integer> {

    private final AdminService adminService;

    public LogoutCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Integer call() {
        try {
            adminService.logout();
            System.out.println("Logged out.");
            return 0;
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            return 1;
        }
    }
}
