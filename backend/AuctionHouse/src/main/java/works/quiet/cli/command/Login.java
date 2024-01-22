package works.quiet.cli.command;

import picocli.CommandLine;
import works.quiet.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

@CommandLine.Command(
        name ="login",
        description = "Login with username and password, and persist session.",
        mixinStandardHelpOptions = true
)
public class Login implements Callable {


    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    @CommandLine.Option(names = {"-u", "--username"}, required = true)
    private String username;

    @CommandLine.Option( names = {"-p", "--password"}, required = true)
    private String password;

    @Override
    public Integer call() throws Exception {
        ExitCode exitCode = new ExitCode(1);

        DBConnection connection = new PGConnection("jdbc:postgresql://localhost:5432/auction-house","grancalavera");
        UserDao userDao = new PGUserDao(connection);

        userDao.findWithCredentials(username, password).ifPresentOrElse((user) ->{
            try {
                Path path = Files.createTempFile("ah-session", null);
                Files.write(path, username.getBytes(), StandardOpenOption.WRITE);
                System.out.printf("Logged in as %s\n",username);
                exitCode.set(0);
            } catch(Exception ex) {
                System.out.println("Unable to authenticate, please try again.");
            }
        }, ()->{
            System.out.println("Wrong username or password.");
        });

        return exitCode.get();
    }

}
