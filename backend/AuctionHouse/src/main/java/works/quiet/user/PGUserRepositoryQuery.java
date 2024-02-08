package works.quiet.user;

import works.quiet.db.PGMapper;
import works.quiet.db.PGRepositoryQuery;
import works.quiet.db.DBConnection;

import java.util.logging.Level;

public class PGUserRepositoryQuery extends PGRepositoryQuery<User> {
    public PGUserRepositoryQuery(
            final Level logLevel, final DBConnection connection, final PGMapper<User> mapper) {
        super(logLevel, connection, mapper);
    }
}
