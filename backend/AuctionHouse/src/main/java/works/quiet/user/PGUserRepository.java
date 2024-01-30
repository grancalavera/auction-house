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

    public PGUserRepository(Level logLevel, DBConnection connection) {
        this.connection = connection;
        log.setLevel(logLevel);
    }

    @Override
    public List<UserModel> listUsers() {
        return queryMany((conn) -> conn.prepareStatement(
                "SELECT u.id, u.username, u.password, u.first_name, u.last_name, a.status_name as account_status, r.role_name as role, u.organisation_id, o.org_name as organisation"
                        + " FROM ah_users u"
                        + " LEFT JOIN ah_organisations o on u.organisation_id = o.id"
                        + " LEFT JOIN ah_accountstatus a on u.account_status_id = a.id"
                        + " LEFT JOIN ah_roles r on u.role_id = r.id"
                        + " ORDER BY id"
        ), this::userFromResultSet);
    }

    @Override
    public Optional<UserModel> findWithCredentials(String username, String password) {
        FunctionThrows<Connection, PreparedStatement, Exception> query;

        query = (conn) -> {
            PreparedStatement st = conn.prepareStatement(
                    "SELECT u.id, u.username, u.password, u.first_name, u.last_name, a.status_name as account_status, r.role_name as role, u.organisation_id, o.org_name as organisation"
                            + " FROM ah_users u"
                            + " LEFT JOIN ah_organisations o on u.organisation_id = o.id"
                            + " LEFT JOIN ah_accountstatus a on u.account_status_id = a.id"
                            + " LEFT JOIN ah_roles r on u.role_id = r.id"
                            + " WHERE u.username=? AND u.password=?"
            );
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
            PreparedStatement st = conn.prepareStatement(
                    "SELECT u.id, u.username, u.password, u.first_name, u.last_name, a.status_name as account_status, r.role_name as role, u.organisation_id, o.org_name as organisation"
                            + " FROM ah_users u"
                            + " LEFT JOIN ah_organisations o on u.organisation_id = o.id"
                            + " LEFT JOIN ah_accountstatus a on u.account_status_id = a.id"
                            + " LEFT JOIN ah_roles r on u.role_id = r.id"
                            + " WHERE u.username=?"
            );
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
                    .password(resultSet.getString("password"))
                    .firstName(resultSet.getString("first_name"))
                    .lastName(resultSet.getString("last_name"))
                    .accountStatus(AccountStatus.valueOf(resultSet.getString("account_status")))
                    .role(Role.valueOf(resultSet.getString("role")))
                    .organisation(OrganisationModel.builder()
                            .id(resultSet.getInt("organisation_id"))
                            .name(resultSet.getString("organisation"))
                            .build())
                    .build();
            log.info(user.toString());
        } catch (Exception ex) {
            log.severe("failed to map UserModel from ResulSet: " + ex);
        }

        return user;
    }
}
