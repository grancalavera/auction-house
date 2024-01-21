package works.quiet.cli.command;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name="create-user",
        description = "Creates a new user.",
        mixinStandardHelpOptions = true
)
public class CreateUser implements Callable {

    @CommandLine.Option(
            names = {"-u", "--username"}, required = true,
            description = "alpha-numerical string (no spaces or special characters)" +
                    ", usernames must be unique"
    )
    private String username;

    @CommandLine.Option(
            names = {"-p", "--password"}, required = true,
            description = "At least 16 characters long"
    )
    private String password;

    @Override
    public Integer call() throws Exception {
        System.out.printf("create-user username=%s password=%s", username, password);
        return 0;
    }
}
