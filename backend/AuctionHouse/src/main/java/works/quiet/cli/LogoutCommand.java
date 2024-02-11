package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;
import java.util.logging.Level;

@CommandLine.Command(
        name = "logout",
        description = "Terminates the current user's session.",
        mixinStandardHelpOptions = true
)
public class LogoutCommand extends CommandWithAdmin {

    public LogoutCommand(final Level logLevel, final Resources resources, final AdminService adminService) {
        super(logLevel, resources, adminService);
    }

    @Override
    public Integer call() throws Exception {
        adminService.logout();
        spec.commandLine().getOut().println("Logged out.");
        return 0;
    }
}
