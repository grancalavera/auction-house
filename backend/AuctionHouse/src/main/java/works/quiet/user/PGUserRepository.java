package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.dao.Dao;
import works.quiet.etc.FunctionThrows;
import works.quiet.io.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Log
public class PGUserRepository implements UserRepository {
    private final Dao<UserModel> userDao;

    // I know this is bad but is because the Dao stuff really doesn't work
    // for mutations, at least not intuitively for me. I'll just do it by
    // hand now and try to figure out how to improve it later. Even maybe
    // way down the line this whole thing will be replaced by an ORM.
    private final DBConnection connection;

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

    public PGUserRepository(Level logLevel, Dao<UserModel> userDao, DBConnection connection) {
        this.userDao = userDao;
        this.connection = connection;
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
            PreparedStatement st = conn.prepareStatement(USERS_QUERY + " WHERE u.username=? AND u.password=?");
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
            var st = conn.prepareStatement(USERS_QUERY + " WHERE u.username=?"
            );
            st.setString(1, username);
            return st;
        };

        var maybeUser = userDao.queryOne(query);
        log.info(maybeUser.toString());
        return maybeUser;
    }

    @Override
    public Optional<UserModel> findById(int id) {
        FunctionThrows<Connection, PreparedStatement, Exception> query;

        query = (conn) -> {
            var st = conn.prepareStatement(USERS_QUERY + " WHERE u.id=?"
            );
            st.setInt(1, id);
            return st;
        };

        var maybeUser = userDao.queryOne(query);
        log.info(maybeUser.toString());
        return maybeUser;
    }

    //insert into ah_organisations (org_name) values (:'org') on conflict (org_name) do nothing;
    @Override
    public int createUser(
            String username,
            String password,
            String firstName,
            String lastName,
            String organisationName,
            int roleId,
            int accountStatusId
    ) throws Exception {
        AtomicReference<Integer> idRef = new AtomicReference<>();
        connection.getConnection().ifPresent(conn -> {
            try (
                    PreparedStatement insertOrg = conn.prepareStatement(
                            "INSERT INTO ah_organisations (org_name) values (?) ON CONFLICT DO NOTHING"
                    );
                    PreparedStatement queryOrgId = conn.prepareStatement(
                            "SELECT id FROM ah_organisations WHERE org_name=?"
                    );
                    PreparedStatement insertUser = conn.prepareStatement(
                            "INSERT INTO ah_users" +
                                    " (username, password, first_name, last_name, organisation_id, account_status_id, role_id) " +
                                    " VALUES " +
                                    " (?, ?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    )
            ) {
                conn.setAutoCommit(false);

                insertOrg.setString(1, organisationName);
                insertOrg.executeUpdate();

                queryOrgId.setString(1, organisationName);
                var orgIdRs = queryOrgId.executeQuery();
                orgIdRs.next();
                var orgId = orgIdRs.getInt("id");

                insertUser.setString(1, username);
                insertUser.setString(2, password);
                insertUser.setString(3, firstName);
                insertUser.setString(4, lastName);
                insertUser.setInt(5, orgId);
                insertUser.setInt(6, accountStatusId);
                insertUser.setInt(7, roleId);
                insertUser.executeUpdate();

                var userRs = insertUser.getGeneratedKeys();
                userRs.next();
                var userId = userRs.getInt("id");
                idRef.set(userId);

                conn.commit();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        return idRef.get();
    }

    @Override
    public void updateUser(UserModel user) throws Exception {
        connection.getConnection().ifPresent(conn -> {
            try (
                    PreparedStatement insertOrg = conn.prepareStatement(
                            "INSERT INTO ah_organisations (org_name) values (?) ON CONFLICT DO NOTHING"
                    );
                    PreparedStatement queryOrgId = conn.prepareStatement(
                            "SELECT id FROM ah_organisations WHERE org_name=?"
                    );
                    PreparedStatement updateUser = conn.prepareStatement(
                            "UPDATE ah_users SET" +
                                    " username=?," +            // 1
                                    " password=?," +            // 2
                                    " first_name=?," +          // 3
                                    " last_name=?," +           // 4
                                    " organisation_id=?," +     // 5
                                    " account_status_id=?," +   // 6
                                    " role_id=?" +              // 7
                                    " WHERE id=?")              // 8
            ) {
                conn.setAutoCommit(false);

                String organisationName = user.getOrganisation().getName();
                String username = user.getUsername();
                String password = user.getPassword();
                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                int accountStatusId = user.getAccountStatus().getId();
                int roleId = user.getRole().getId();
                int userId = user.getId();

                insertOrg.setString(1, organisationName);
                insertOrg.executeUpdate();

                queryOrgId.setString(1, organisationName);
                var orgIdRs = queryOrgId.executeQuery();
                orgIdRs.next();
                var orgId = orgIdRs.getInt("id");

                updateUser.setString(1, username);
                updateUser.setString(2, password);
                updateUser.setString(3, firstName);
                updateUser.setString(4, lastName);
                updateUser.setInt(5, orgId);
                updateUser.setInt(6, accountStatusId);
                updateUser.setInt(7, roleId);
                updateUser.setInt(8, userId);
                updateUser.executeUpdate();

                conn.commit();
            } catch (SQLException ex) {
                log.severe(ex.toString());
                throw new RuntimeException(ex);
            }
        });
    }
}
