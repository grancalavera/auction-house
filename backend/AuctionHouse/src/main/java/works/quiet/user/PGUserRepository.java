package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.db.PGMapper;
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
    private final DBConnection connection; // still need this for mutations, but maybe I can move into the query "proxy"
    private final RepositoryQuery<User> userRepoQuery;
    private final PGMapper<User> mapper;

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
            final Level logLevel, final RepositoryQuery<User> userRepoQuery, final DBConnection connection,
            final PGMapper<User> mapper) {
        this.userRepoQuery = userRepoQuery;
        this.connection = connection;
        this.mapper = mapper;
        log.setLevel(logLevel);
    }

    @Override
    public List<User> findAll() {
        return userRepoQuery.queryMany(
                (conn) -> conn.prepareStatement(usersQuery + " ORDER BY id"),
                mapper::fromResulSet
        );
    }

    @Override
    public long count() {
        return userRepoQuery.queryCount(conn -> conn.prepareStatement("SELECT count(id) FROM users"));
    }

    @Override
    public boolean exists(final int id) {
        return userRepoQuery.queryExists(conn -> {
            var st = conn.prepareStatement("SELECT id FROM users WHERE id=?");
            st.setInt(1, id);
            return st;
        });
    }

    @Override
    public Optional<User> findWithCredentials(final String username, final String password) {
        return userRepoQuery.queryOne(
                (conn) -> {
                    PreparedStatement st = conn.prepareStatement(usersQuery + " WHERE u.username=? AND u.password=?");
                    st.setString(1, username);
                    st.setString(2, password);
                    return st;
                },
                mapper::fromResulSet
        );
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        return userRepoQuery.queryOne(
                (conn) -> {
                    var st = conn.prepareStatement(usersQuery + " WHERE u.username=?"
                    );
                    st.setString(1, username);
                    return st;
                },
                mapper::fromResulSet);
    }

    @Override
    public Optional<User> findOne(final int id) {
        return userRepoQuery.queryOne(
                (conn) -> {
                    var st = conn.prepareStatement(usersQuery + " WHERE u.id=?"
                    );
                    st.setInt(1, id);
                    return st;
                },
                mapper::fromResulSet
        );
    }

    private int createUser(final User user) throws Exception {
        AtomicReference<Integer> idRef = new AtomicReference<>();
        connection.getConnection().ifPresent(conn -> {
            try (
                    PreparedStatement st = conn.prepareStatement(
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
                var username = user.getUsername();
                var password = user.getPassword();
                var firstName = user.getFirstName();
                var lastName = user.getLastName();
                var orgId = user.getOrganisation().getId();
                var accountStatusId = user.getAccountStatus().getId();
                var roleId = user.getRole().getId();

                setStatementValues(
                        st,
                        username,
                        password,
                        firstName,
                        lastName,
                        orgId,
                        accountStatusId,
                        roleId
                );
                st.executeUpdate();

                var userRs = st.getGeneratedKeys();
                userRs.next();
                var userId = userRs.getInt("id");
                idRef.set(userId);
            } catch (final SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        return idRef.get();
    }

    private void updateUser(final User user) throws Exception {
        connection.getConnection().ifPresent(conn -> {
            try (
                    PreparedStatement st = conn.prepareStatement(
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
                var username = user.getUsername();
                var password = user.getPassword();
                var firstName = user.getFirstName();
                var lastName = user.getLastName();
                var orgId = user.getOrganisation().getId();
                var accountStatusId = user.getAccountStatus().getId();
                var roleId = user.getRole().getId();
                var userId = user.getId();

                setStatementValues(st,
                        username,
                        password,
                        firstName,
                        lastName,
                        orgId,
                        accountStatusId,
                        roleId,
                        userId
                );

                st.executeUpdate();
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

    @Override
    public void delete(final User entity) throws Exception {
        connection.getConnection().ifPresent(conn -> {
            try (
                PreparedStatement st = conn.prepareStatement("DELETE FROM users WHERE id=?");
            ) {
                st.setObject(1, entity.getId());
                st.executeUpdate();
            } catch (final SQLException ex) {
                log.severe(ex.toString());
                throw new RuntimeException(ex);
            }
        });
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
