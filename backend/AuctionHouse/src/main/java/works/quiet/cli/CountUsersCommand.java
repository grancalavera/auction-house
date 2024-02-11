package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "count-users",
        description = "Counts all existing users.",
        mixinStandardHelpOptions = true
)
public class CountUsersCommand implements Callable<Integer> {

    private final AdminService adminService;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public CountUsersCommand(final AdminService adminService) {
        this.adminService = adminService;
    }


    @Override
    public Integer call() throws Exception {
        adminService.assertIsAdmin();
        adminService.assertIsNotBlocked();
        var count = adminService.countUsers();
        spec.commandLine().getOut().println(count);
        return 0;
    }
}
