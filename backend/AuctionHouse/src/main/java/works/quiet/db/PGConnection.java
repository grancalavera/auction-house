package works.quiet.db;

import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGConnection implements DBConnection {
    private final String url;
    private final String username;
    private final String password;
    private Connection connection;

    public PGConnection(final Level logLevel, final String url, final String username, final String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        log.setLevel(logLevel);
    }

    @Override
    public Optional<Connection> getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url, username, password);
            } catch (final SQLException ex) {
                log.severe("Failed to open postgres connection.");
                log.severe(ex.toString());
            }
        }

        return Optional.ofNullable(connection);
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (final SQLException ex) {
                log.severe("Failed to close postgres connection.");
                log.severe(ex.toString());
                throw new RuntimeException(ex.getMessage());
            }
        }
    }
}
