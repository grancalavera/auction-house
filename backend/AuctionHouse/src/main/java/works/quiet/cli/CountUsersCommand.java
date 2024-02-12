package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "count-users",
        description = "Counts all existing users.",
        mixinStandardHelpOptions = true
)
public class CountUsersCommand extends CommandWithAdmin {

    public CountUsersCommand(final Level logLevel, final Resources resources, final AdminService adminService) {
        super(logLevel, resources, adminService);
    }


    @Override
    public void run() {
        adminService.assertIsAdmin();
        adminService.assertIsNotBlocked();
        var count = adminService.countUsers();
        spec.commandLine().getOut().println(count);
    }
}
