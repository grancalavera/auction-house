package works.quiet.cli;

import lombok.extern.java.Log;
import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;
import java.util.logging.Level;

@Log
@CommandLine.Command(
        name = "whoami",
        description = "Prints the username of the currently logged in user. If there is no authenticated user, the command exits with an error code.",
        mixinStandardHelpOptions = true)
public class WhoAmICommand implements Callable<Integer> {
    final AdminService adminService;

    public WhoAmICommand(Level logLevel ,AdminService adminService) {
        this.adminService = adminService;
        log.setLevel(logLevel);
    }

    @Override
    public Integer call()  {
        final var maybeUser = adminService.getCurrentUser();

        if (maybeUser.isEmpty()) {
            log.severe("Not authenticated.");
            return 1;
        }

        System.out.println(maybeUser.get());
        return 0;
    }
}
