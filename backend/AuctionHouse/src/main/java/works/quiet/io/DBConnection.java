package works.quiet.io;

import java.sql.Connection;
import java.util.Optional;

public interface DBConnection extends AutoCloseable {
    public Optional<Connection> getConnection();
}
