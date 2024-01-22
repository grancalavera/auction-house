package works.quiet.io;

import lombok.extern.java.Log;
import works.quiet.domain.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Log
public class PGUserDao implements UserDao{
    private final DBConnection connection;

    public PGUserDao(DBConnection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<User> findWithCredentials(String username, String password) {
        AtomicReference<User> nullableUser = new AtomicReference<User>(null);

        String query = "SELECT username FROM ah_users WHERE username='" + username + "' AND password='" + password +"' LIMIT 1";

        connection.getConnection().ifPresent(conn->{
            try (ResultSet resultSet = conn.createStatement().executeQuery(query)){
                if (resultSet.next()) {
                    User user = deserialize(resultSet);
                    nullableUser.set(user);
                }
            }catch(SQLException ex){
                log.severe("failed to findWithCredentials:" + ex.toString());
            }
        });

        return Optional.ofNullable(nullableUser.get());
    }

    private User deserialize(ResultSet resultSet) {
        User user = null;

        try {
            User.UserBuilder builder = User.builder();
//            builder.id(resultSet.getInt("id"));
            user = builder.build();

        } catch (Exception ex) {
            log.severe("failed to deserialize user: " + ex.toString());
        }

        return user;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
