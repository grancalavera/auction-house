package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.db.RepositoryQuery;
import works.quiet.db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Log
public class PGUserRepository implements UserRepository {
    private final RepositoryQuery<User> userRepositoryQuery;

    // I know this is bad but is because the Dao stuff really doesn't work
    // for mutations, at least not intuitively for me. I'll just do it by
    // hand now and try to figure out how to improve it later. Even maybe
    // way down the line this whole thing will be replaced by an ORM.
    private final DBConnection connection;

    private final String usersQuery =
            "SELECT"
                    + " u.id, u.username,"
                    + " u.password,"
                    + " u.first_name,"
                    + " u.last_name,"
                    + " a.status_name as account_status,"
                    + " r.role_name as role,"
                    + " u.organisation_id,"
                    + " o.org_name as organisation"
                    + " FROM ah_users u"
                    + " LEFT JOIN ah_organisations o on u.organisation_id = o.id"
                    + " LEFT JOIN ah_accountstatus a on u.account_status_id = a.id"
                    + " LEFT JOIN ah_roles r on u.role_id = r.id";

    public PGUserRepository(
            final Level logLevel, final RepositoryQuery<User> userRepositoryQuery, final DBConnection connection) {
        this.userRepositoryQuery = userRepositoryQuery;
        this.connection = connection;
        log.setLevel(logLevel);
    }

    @Override
    public List<User> findAll() {
        return userRepositoryQuery.queryMany((conn) -> conn.prepareStatement(usersQuery + " ORDER BY id"));
    }

    @Override
    public Optional<User> findWithCredentials(final String username, final String password) {
        return userRepositoryQuery.queryOne((conn) -> {
            PreparedStatement st = conn.prepareStatement(usersQuery + " WHERE u.username=? AND u.password=?");
            st.setString(1, username);
            st.setString(2, password);
            return st;
        });
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        return userRepositoryQuery.queryOne((conn) -> {
            var st = conn.prepareStatement(usersQuery + " WHERE u.username=?"
            );
            st.setString(1, username);
            return st;
        });
    }

    @Override
    public Optional<User> findOne(final int id) {
        return userRepositoryQuery.queryOne((conn) -> {
            var st = conn.prepareStatement(usersQuery + " WHERE u.id=?"
            );
            st.setInt(1, id);
            return st;
        });
    }

    private int createUser(final User user) throws Exception {
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
                            "INSERT INTO ah_users ("
                                    + "username,"
                                    + " password,"
                                    + " first_name,"
                                    + " last_name,"
                                    + " organisation_id,"
                                    + " account_status_id"
                                    + ", role_id"
                                    + ") "
                                    + " VALUES "
                                    + " (?, ?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    )
            ) {
                conn.setAutoCommit(false);

                String organisationName = user.getOrganisation().getName();
                String username = user.getUsername();
                String password = user.getPassword();
                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                int accountStatusId = user.getAccountStatus().getId();
                int roleId = user.getRole().getId();

                insertOrg.setString(1, organisationName);
                insertOrg.executeUpdate();

                queryOrgId.setString(1, organisationName);
                var orgIdRs = queryOrgId.executeQuery();
                orgIdRs.next();
                var orgId = orgIdRs.getInt("id");

                setStatementValues(
                        insertUser,
                        username,
                        password,
                        firstName,
                        lastName,
                        orgId,
                        accountStatusId,
                        roleId
                );
                insertUser.executeUpdate();

                var userRs = insertUser.getGeneratedKeys();
                userRs.next();
                var userId = userRs.getInt("id");
                idRef.set(userId);

                conn.commit();
            } catch (final SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        return idRef.get();
    }

    private void updateUser(final User user) throws Exception {
        connection.getConnection().ifPresent(conn -> {
            try (
                    PreparedStatement insertOrg = conn.prepareStatement(
                            "INSERT INTO ah_organisations (org_name) values (?) ON CONFLICT DO NOTHING"
                    );
                    PreparedStatement queryOrgId = conn.prepareStatement(
                            "SELECT id FROM ah_organisations WHERE org_name=?"
                    );
                    PreparedStatement updateUser = conn.prepareStatement(
                            "UPDATE ah_users SET"
                                    + " username=?,"            // 1
                                    + " password=?,"            // 2
                                    + " first_name=?,"          // 3
                                    + " last_name=?,"           // 4
                                    + " organisation_id=?,"     // 5
                                    + " account_status_id=?,"   // 6
                                    + " role_id=?"              // 7
                                    + " WHERE id=?")            // 8
            ) {
                conn.setAutoCommit(false);

                String organisationName = user.getOrganisation().getName();
                String username = user.getUsername();
                String password = user.getPassword();
                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                int accountStatusId = user.getAccountStatus().getId();
                int roleId = user.getRole().getId();

                insertOrg.setString(1, organisationName);
                insertOrg.executeUpdate();

                queryOrgId.setString(1, organisationName);
                var orgIdRs = queryOrgId.executeQuery();
                orgIdRs.next();
                var orgId = orgIdRs.getInt("id");
                int userId = user.getId();

                setStatementValues(updateUser,
                        username,
                        password,
                        firstName,
                        lastName,
                        orgId,
                        accountStatusId,
                        roleId,
                        userId
                );

                updateUser.executeUpdate();

                conn.commit();
            } catch (final SQLException ex) {
                log.severe(ex.toString());
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public User save(final User user) throws Exception {
        if (user.getId() == Integer.MIN_VALUE) {
            var id = createUser(user);
            return user.toBuilder().id(id).build();
        } else {
            updateUser(user);
            return user.toBuilder().build();
        }
    }

    // https://stackoverflow.com/a/2563492
    // https://balusc.omnifaces.org/2008/07/dao-tutorial-data-layer.html
    // will extract to somewhere later on...
    private void setStatementValues(final PreparedStatement st, final Object... values) throws SQLException {
        if (values == null) {
            return;
        }

        for (int i = 0; i < values.length; i++) {
            st.setObject(i + 1, values[i]);
        }
    }
}
