package works.quiet.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

// implements AutoCloseable
public class JdbcConnection{
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    // Optional allocates so for low latency is not ok
    // Also is a reference
    // low latency uses null
    // can be garbage collected so too many allocs can hicup gc (stop the world pause)
    // Optional: reference data: maybe ok, matching engine: bad bad
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

//    @Override
//    public void close() throws Exception {
//        connection.ifPresent(conn ->{
//            conn.close();
//        });
//    }
}

