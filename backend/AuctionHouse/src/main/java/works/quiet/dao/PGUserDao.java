package works.quiet.dao;

import works.quiet.io.DBConnection;
import works.quiet.user.UserModel;

import java.util.logging.Level;

public class PGUserDao extends PGDao<UserModel> {
    public PGUserDao(final Level logLevel, final DBConnection connection, final PGDaoMapper<UserModel> mapper) {
        super(logLevel, connection, mapper);
    }
}
