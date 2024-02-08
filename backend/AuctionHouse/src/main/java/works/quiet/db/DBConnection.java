package works.quiet.db;

import java.sql.Connection;
import java.util.Optional;

public interface DBConnection extends AutoCloseable {
    Optional<Connection> getConnection();
}
