package works.quiet.io;

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
    private Connection connection = null;

    public PGConnection(Level logLevel, String url, String username, String password) {
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
            } catch (SQLException ex) {
                log.severe("Failed to open postgres connection.");
                log.severe(ex.toString());
            }
        }

        return Optional.ofNullable(connection);
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException ex) {
                log.severe("Failed to close postgres connection.");
                throw ex;
            }
        }
    }
}
