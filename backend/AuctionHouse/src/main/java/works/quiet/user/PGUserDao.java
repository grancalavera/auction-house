package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.io.DBConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Log
public class PGUserDao implements UserDao {
    private final DBConnection connection;

    public PGUserDao(DBConnection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<UserModel> findWithCredentials(String username, String password) {
        AtomicReference<UserModel> nullableUser = new AtomicReference<UserModel>(null);

        String query = "SELECT username FROM ah_users WHERE username='" + username + "' AND password='" + password +"' LIMIT 1";

        connection.getConnection().ifPresent(conn->{
            try (ResultSet resultSet = conn.createStatement().executeQuery(query)){
                if (resultSet.next()) {
                    UserModel user = deserialize(resultSet);
                    nullableUser.set(user);
                }
            }catch(SQLException ex){
                log.severe("failed to findWithCredentials:" + ex.toString());
            }
        });

        return Optional.ofNullable(nullableUser.get());
    }

    private UserModel deserialize(ResultSet resultSet) {
        UserModel user = null;

        try {
            UserModel.UserModelBuilder builder = UserModel.builder();
            user = builder.build();
        } catch (Exception ex) {
            log.severe("failed to deserialize user: " + ex.toString());
        }

        return user;
    }

}
