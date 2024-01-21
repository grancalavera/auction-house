package works.quiet.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcConnection {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());
    private static Optional<Connection> connection = Optional.empty();

    public static Optional<Connection> getConnection() {
        if (connection.isEmpty()) {
            String url = "jdbc:postgresql://localhost:5432/auction-house";
            String username = "grancalavera";
            try {
                connection = Optional.ofNullable(
                        DriverManager.getConnection(url, username, null)
                );
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Failed to open DB connection.", ex);
            }
        }
        return connection;
    }
}

