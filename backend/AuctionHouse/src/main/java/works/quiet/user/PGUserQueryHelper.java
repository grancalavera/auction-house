package works.quiet.user;

import works.quiet.db.DBConnection;
import works.quiet.db.PGQueryHelper;

import java.util.logging.Level;

public class PGUserQueryHelper extends PGQueryHelper<User> {
    public PGUserQueryHelper(final Level logLevel, final DBConnection connection) {
        super(logLevel, connection);
    }
}
