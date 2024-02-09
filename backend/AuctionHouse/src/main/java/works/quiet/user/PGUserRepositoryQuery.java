package works.quiet.user;

import works.quiet.db.DBConnection;
import works.quiet.db.PGRepositoryQuery;

import java.util.logging.Level;

public class PGUserRepositoryQuery extends PGRepositoryQuery<User> {
    public PGUserRepositoryQuery(final Level logLevel, final DBConnection connection) {
        super(logLevel, connection);
    }
}
