package works.quiet.cli;

import lombok.extern.java.Log;
import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;
import java.util.logging.Level;

@Log
@CommandLine.Command(
        name = "whoami",
        description = "Prints the username of the currently logged in user.",
        mixinStandardHelpOptions = true)
public class WhoAmICommand implements Callable<Integer> {
    final AdminService adminService;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public WhoAmICommand(final Level logLevel, final AdminService adminService) {
        this.adminService = adminService;
        log.setLevel(logLevel);
    }

    @Override
    public Integer call() throws Exception {
        adminService.assertIsNotBlocked();
        spec.commandLine().getOut().println(adminService.getCurrentUsername());
        return 0;
    }
}
