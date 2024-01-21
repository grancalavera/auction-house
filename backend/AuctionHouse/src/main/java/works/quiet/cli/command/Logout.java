package works.quiet.cli.command;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "logout",
        description = "Terminates the current user's session.",
        mixinStandardHelpOptions = true
)
public class Logout implements Callable {
    @Override
    public Object call() throws Exception {
        System.out.println("logout");
        return 0;
    }
}
