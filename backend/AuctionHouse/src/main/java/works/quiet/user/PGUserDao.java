package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.io.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Map;

@Log
public class PGUserDao implements UserDao {
    private final DBConnection connection;
    private final Map<Integer, OrganisationModel> organisations;

    public PGUserDao(DBConnection connection, Map<Integer, OrganisationModel> organisations) {
        this.connection = connection;
        this.organisations = organisations;
    }

    @Override
    public Optional<UserModel> findWithCredentials(String username, String password) {
        AtomicReference<UserModel> nullableUser = new AtomicReference<UserModel>(null);

        String query = "SELECT * FROM ah_users WHERE username='" + username + "' AND password='" + password + "'";

        connection.getConnection().ifPresent(conn->{
            try (
                    Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery(query)
            ){
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
            user = UserModel
                    .builder()
                    .id(resultSet.getInt("id"))
                    .username(resultSet.getString("username"))
                    .password(resultSet.getString("password"))
                    .firstName(resultSet.getString("first_name"))
                    .lastName(resultSet.getString("last_name"))
                    .accountStatus(AccountStatus.ofInt(resultSet.getInt("account_status_id")))
                    .role(Role.ofInt(resultSet.getInt("role_id")))
                    .organisation(organisations.get(resultSet.getInt("organisation_id")))
                    .build();
            log.info(user.toString());
        } catch (Exception ex) {
            log.severe("failed to deserialize user: " + ex.toString());
        }

        return user;
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        return Optional.empty();
    }
}
