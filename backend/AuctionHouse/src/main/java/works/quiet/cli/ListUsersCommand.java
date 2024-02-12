package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;
import java.util.logging.Level;

@CommandLine.Command(
        name = "list-users",
        description = "List all existing users.",
        mixinStandardHelpOptions = true
)
public class ListUsersCommand extends CommandWithAdmin {


    public ListUsersCommand(final Level logLevel, final Resources resources, final AdminService adminService) {
        super(logLevel, resources, adminService);
    }

    @Override
    public Integer call() throws Exception {
        adminService.assertIsAdmin();
        adminService.assertIsNotBlocked();
        adminService.listUsers().forEach(System.out::println);
        return 0;
    }
}
