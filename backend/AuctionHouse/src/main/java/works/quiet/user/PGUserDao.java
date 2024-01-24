package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.io.DBConnection;
import works.quiet.reference.OrganisationModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@FunctionalInterface
interface ThrowingFunction<T, R, E extends Exception> {
    R apply(T t) throws E;
}
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
        ArrayList<UserModel> result = queryUsers((conn) -> {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM ah_users WHERE username=? AND password=?");
            st.setString(1, username);
            st.setString(2, password);
            return st;
        });
        return Optional.ofNullable(result.getFirst());
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        ArrayList<UserModel> result = queryUsers((conn) -> {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM ah_users WHERE username=?");
            st.setString(1, username);
            return st;
        });
        return Optional.ofNullable(result.getFirst());
    }

    private ArrayList<UserModel> queryUsers(ThrowingFunction<Connection, PreparedStatement, Exception>statement) {
        ArrayList<UserModel> users = new ArrayList<>();
        connection.getConnection().ifPresent(conn-> {
            try {
                PreparedStatement st = statement.apply(conn);
                ResultSet rs = st.executeQuery();

                while (rs.next()) {
                    users.add(deserialize(rs));
                }

                st.close();
                rs.close();
            } catch (Exception ex) {
                log.severe(ex.toString());
            }
        });
        return users;
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
}
