package works.quiet.cli.command;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name ="login",
        description = "Login with username and password",
        mixinStandardHelpOptions = true
)
public class Login implements Callable {
    @CommandLine.Option(names = {"-u", "--username"}, required = true)
    private String username;

    @CommandLine.Option( names = {"-p", "--password"}, required = true)
    private String password;

    @Override
    public Integer call() throws Exception {
        System.out.printf("login username=%s password=%s\n", username, password);
        return 0;
    }
}
