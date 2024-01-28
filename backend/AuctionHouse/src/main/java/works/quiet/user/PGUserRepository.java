package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.etc.FunctionThrows;
import works.quiet.io.DBConnection;
import works.quiet.reference.OrganisationModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Log
public class PGUserRepository implements UserRepository {
    private final DBConnection connection;
    private final Map<Integer, OrganisationModel> organisations;

    public PGUserRepository(Level logLevel, DBConnection connection, Map<Integer, OrganisationModel> organisations) {
        this.connection = connection;
        this.organisations = organisations;
        log.setLevel(logLevel);
    }


    @Override
    public List<UserModel> listUsers() {
        return queryMany((conn) -> conn.prepareStatement("SELECT * FROM ah_users ORDER BY id"), this::userFromResultSet);
    }

    @Override
    public Optional<UserModel> findWithCredentials(String username, String password) {
        FunctionThrows<Connection, PreparedStatement, Exception> query;

        query = (conn) -> {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM ah_users WHERE username=? AND password=?");
            st.setString(1, username);
            st.setString(2, password);
            return st;
        };

        var maybeUser = queryOne(query, this::userFromResultSet);
        log.info(maybeUser.toString());
        return maybeUser;
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        FunctionThrows<Connection, PreparedStatement, Exception> query;

        query = (conn) -> {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM ah_users WHERE username=?");
            st.setString(1, username);
            return st;
        };

        var maybeUser = queryOne(query, this::userFromResultSet);
        log.info(maybeUser.toString());
        return maybeUser;
    }

    @Override
    public Optional<Integer> createUser(UserModel prototype) {
        log.info("Create user from prototype: " + prototype);

        // refactor to use a wrapper class around int
        AtomicReference<Integer> idRef = new AtomicReference<>();
        FunctionThrows<Connection, PreparedStatement, Exception> mutation;

        var org = prototype.getOrganisation();

        mutation = (conn -> {
            PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO ah_users ("
                            + "username, password, first_name, last_name, organisation_id, account_status_id, role_id"
                            + ") VALUES (?, ?, ?, ?, ?, ?, ?)"
                    , Statement.RETURN_GENERATED_KEYS
            );
            st.setString(1, prototype.getUsername());
            st.setString(2, prototype.getPassword());
            st.setString(3, prototype.getFirstName());
            st.setString(4, prototype.getLastName());
            st.setInt(5, org == null ? OrganisationModel.UNKNOWN_ORGANISATION : org.getId());
            st.setInt(6, prototype.getAccountStatus().getId());
            st.setInt(7, prototype.getRole().getId());

            return st;
        });

        connection.getConnection().ifPresent(conn -> {
            try (
                    PreparedStatement st = mutation.apply(conn)
            ) {
                int rowsInserted = st.executeUpdate();
                log.info(rowsInserted + " rows inserted.");
                if (rowsInserted > 0) {
                    try (ResultSet rs = st.getGeneratedKeys()) {
                        if (rs.next()) {
                            var key = rs.getInt("id");
                            idRef.set(key);
                        }
                    } catch (Exception ex) {
                        log.severe(ex.toString());
                    }
                }
            } catch (Exception ex) {
                log.severe("Failed to create use from prototype: " + ex);
            }
        });

        return Optional.ofNullable(idRef.get());
    }

    private List<UserModel> queryMany(
            FunctionThrows<Connection, PreparedStatement, Exception> statement,
            FunctionThrows<ResultSet, UserModel, Exception> mapper) {

        List<UserModel> users = new ArrayList<>();

        connection.getConnection().ifPresent(conn -> {
            try (PreparedStatement st = statement.apply(conn); ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    users.add(mapper.apply(rs));
                }
            } catch (Exception ex) {
                log.severe(ex.toString());
            }
        });

        log.info(users.toString());
        return users;
    }

    private Optional<UserModel> queryOne(
            FunctionThrows<Connection, PreparedStatement, Exception> statement,
            FunctionThrows<ResultSet, UserModel, Exception> mapper) {

        List<UserModel> queryResult = queryMany(statement, mapper);

        if (queryResult.isEmpty()) {
            return Optional.empty();
        }

        UserModel user = queryResult.getFirst();
        log.info("query result: " + user);

        return Optional.of(user);
    }


    private UserModel userFromResultSet(ResultSet resultSet) {
        UserModel user = null;

        try {
            user = UserModel.builder()
                    .id(resultSet.getInt("id"))
                    .username(resultSet.getString("username"))
                    .password("* redacted *")
                    .firstName(resultSet.getString("first_name"))
                    .lastName(resultSet.getString("last_name"))
                    .accountStatus(AccountStatus.ofInt(resultSet.getInt("account_status_id")))
                    .role(Role.ofInt(resultSet.getInt("role_id")))
                    .organisation(organisations.get(resultSet.getInt("organisation_id")))
                    .build();
            log.info(user.toString());
        } catch (Exception ex) {
            log.severe("failed to map UserModel from ResulSet: " + ex);
        }

        return user;
    }
}
