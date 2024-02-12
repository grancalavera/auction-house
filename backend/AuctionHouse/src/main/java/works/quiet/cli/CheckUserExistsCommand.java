package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "check-user-exists",
        description = "Checks whether an user exists or not.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class CheckUserExistsCommand extends CommandWithAdmin {

    @CommandLine.Parameters(paramLabel = "USER_ID", description = "The user id to check.")
    private int userId;

    public CheckUserExistsCommand(final Level logLevel, final Resources resources, final AdminService adminService) {
        super(logLevel, resources, adminService);
    }


    @Override
    public void run() {
        adminService.assertIsNotBlocked();
        adminService.assertIsAdmin();
        var exists = adminService.userExists(userId);
        spec.commandLine().getOut().println(exists);
    }
}
