package works.quiet.cli.command;

import picocli.CommandLine;
import works.quiet.io.JdbcConnection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
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
        AtomicReference<Integer> exitCode = new AtomicReference<Integer>(1);

        JdbcConnection.getConnection().ifPresent(conn -> {
            String query = "SELECT username FROM ah_users WHERE username='" + username + "' AND password='" + password +"' LIMIT 1";

            try (
                ResultSet resultSet = conn.createStatement().executeQuery(query)
            ) {
                if (resultSet.next()) {
                    System.out.printf("Logged in as %s\n", resultSet.getString("username"));
                    exitCode.set(0);
                }
            } catch(SQLException ex){
                System.out.println("Wrong username or password.");
            }
        });

        if (exitCode.get() == 0) {
            try {
                String content = username;
                Path path = Files.createTempFile("authenticated", null);
                Files.write(path, content.getBytes(), StandardOpenOption.WRITE);
            } catch(Exception ex) {
                System.out.println("Unable to authenticate, please try again.");
                exitCode.set(1);
            }
        }

        return exitCode.get();
    }
}
