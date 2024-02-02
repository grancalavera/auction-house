package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.dao.PGDao;
import works.quiet.etc.FunctionThrows;
import works.quiet.io.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGUserRepository implements UserRepository {
    private final DBConnection connection;
    private final PGDao<UserModel> userDao;

    private final String USERS_QUERY =
            "SELECT" +
                    " u.id, u.username," +
                    " u.password," +
                    " u.first_name," +
                    " u.last_name," +
                    " a.status_name as account_status," +
                    " r.role_name as role," +
                    " u.organisation_id," +
                    " o.org_name as organisation"
                    + " FROM ah_users u"
                    + " LEFT JOIN ah_organisations o on u.organisation_id = o.id"
                    + " LEFT JOIN ah_accountstatus a on u.account_status_id = a.id"
                    + " LEFT JOIN ah_roles r on u.role_id = r.id";

    public PGUserRepository(Level logLevel, DBConnection connection, PGDao<UserModel> userDao) {
        this.connection = connection;
        this.userDao = userDao;
        log.setLevel(logLevel);
    }

    @Override
    public List<UserModel> listUsers() {
        return userDao.queryMany((conn) -> conn.prepareStatement(USERS_QUERY + " ORDER BY id"));
    }

    @Override
    public Optional<UserModel> findWithCredentials(String username, String password) {
        FunctionThrows<Connection, PreparedStatement, Exception> query;

        query = (conn) -> {
            PreparedStatement st = conn.prepareStatement(USERS_QUERY + " WHERE u.username=? AND u.password=?"
            );
            st.setString(1, username);
            st.setString(2, password);
            return st;
        };

        var maybeUser = userDao.queryOne(query);
        log.info(maybeUser.toString());
        return maybeUser;
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        FunctionThrows<Connection, PreparedStatement, Exception> query;

        query = (conn) -> {
            PreparedStatement st = conn.prepareStatement(USERS_QUERY + " WHERE u.username=?"
            );
            st.setString(1, username);
            return st;
        };

        var maybeUser = userDao.queryOne(query);
        log.info(maybeUser.toString());
        return maybeUser;
    }

    @Override
    public int createUser(String username, String password, String firstName, String lastName, String organisationName, int roleId, int accountStatusId) throws Exception {
        throw new Exception("Not Implemented.");
    }

//    @Override
//    public Optional<Integer> createUser(UserModel prototype) {
//        log.info("Create user from prototype: " + prototype);
//
//        AtomicReference<Integer> idRef = new AtomicReference<>();
//        FunctionThrows<Connection, PreparedStatement, Exception> mutation;
//
//        var org = prototype.getOrganisation();
//
//        mutation = (conn -> {
//            PreparedStatement st = conn.prepareStatement(
//                    "INSERT INTO ah_users ("
//                            + "username, password, first_name, last_name, organisation_id, account_status_id, role_id"
//                            + ") VALUES (?, ?, ?, ?, ?, ?, ?)"
//                    , Statement.RETURN_GENERATED_KEYS
//            );
//            st.setString(1, prototype.getUsername());
//            st.setString(2, prototype.getPassword());
//            st.setString(3, prototype.getFirstName());
//            st.setString(4, prototype.getLastName());
//            st.setInt(5, org == null ? OrganisationModel.UNKNOWN_ORGANISATION : org.getId());
//            st.setInt(6, prototype.getAccountStatus().getId());
//            st.setInt(7, prototype.getRole().getId());
//
//            return st;
//        });
//
//        connection.getConnection().ifPresent(conn -> {
//            try (
//                    PreparedStatement st = mutation.apply(conn)
//            ) {
//                int rowsInserted = st.executeUpdate();
//                log.info(rowsInserted + " rows inserted.");
//                if (rowsInserted > 0) {
//                    try (ResultSet rs = st.getGeneratedKeys()) {
//                        if (rs.next()) {
//                            var key = rs.getInt("id");
//                            idRef.set(key);
//                        }
//                    } catch (Exception ex) {
//                        log.severe(ex.toString());
//                    }
//                }
//            } catch (Exception ex) {
//                log.severe("Failed to createOne use from prototype: " + ex);
//            }
//        });
//
//        return Optional.ofNullable(idRef.get());
//    }
}
