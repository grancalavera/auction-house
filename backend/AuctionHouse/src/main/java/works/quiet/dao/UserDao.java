package works.quiet.dao;

import works.quiet.io.DBConnection;
import works.quiet.user.UserModel;

import java.util.logging.Level;

public class UserDao extends PGDao<UserModel> {
    public UserDao(Level logLevel, DBConnection connection, PGDaoMapper<UserModel> mapper) {
        super(logLevel, connection, mapper);
    }

}
