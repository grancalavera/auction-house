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
    private final DBConnection connection;
    private final RepositoryQuery<User> userRepositoryQuery;

    private final String usersQuery =
            "SELECT"
                    + " u.id,"
                    + " u.username,"
                    + " u.password,"
                    + " u.firstname,"
                    + " u.lastname,"
                    + " a.name as accountStatus,"
                    + " r.name as role,"
                    + " u.organisation_id as organisationId,"
                    + " o.name as organisation"
                    + " FROM users u"
                    + " LEFT JOIN organisations o on u.organisation_id = o.id"
                    + " LEFT JOIN account_status a on u.accountstatus_id = a.id"
                    + " LEFT JOIN roles r on u.role_id = r.id";

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
                            "INSERT INTO organisations (name) values (?) ON CONFLICT DO NOTHING"
                    );
                    PreparedStatement queryOrgId = conn.prepareStatement(
                            "SELECT id FROM organisations WHERE name=?"
                    );
                    PreparedStatement insertUser = conn.prepareStatement(
                            "INSERT INTO users ("
                                    + "username,"
                                    + " password,"
                                    + " firstName,"
                                    + " lastName,"
                                    + " organisation_id,"
                                    + " accountStatus_id,"
                                    + " role_id"
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
                            "INSERT INTO organisations (org_name) values (?) ON CONFLICT DO NOTHING"
                    );
                    PreparedStatement queryOrgId = conn.prepareStatement(
                            "SELECT id FROM organisations WHERE org_name=?"
                    );
                    PreparedStatement updateUser = conn.prepareStatement(
                            "UPDATE users SET"
                                    + " username=?,"
                                    + " password=?,"
                                    + " firstName=?,"
                                    + " lastName=?,"
                                    + " organisation_id=?,"
                                    + " accountStatus_id=?,"
                                    + " role_id=?"
                                    + " WHERE id=?")
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
