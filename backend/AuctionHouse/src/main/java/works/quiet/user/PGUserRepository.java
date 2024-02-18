package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.db.DBConnection;
import works.quiet.db.UpsertHelper;
import works.quiet.db.PGMapper;
import works.quiet.db.RepositoryQuery;

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
    public Optional<User> findById(final int id) {
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

    @Override
    public User save(final User user) {
        AtomicReference<Integer> idRef = new AtomicReference<>();

        var helper = new UpsertHelper(
                user.getId() == 0,
                new String[]{
                        "id",
                        "username",
                        "password",
                        "firstName",
                        "lastName",
                        "organisation_id",
                        "role_id",
                        "accountStatus_id"
                },
                new Object[]{
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getOrganisation().getId(),
                        user.getAccountStatus().getId(),
                        user.getRole().getId()
                });

        String sql = "INSERT INTO users (" + helper.getFieldNames() + ")"
                + " VALUES (" + helper.getPlaceholders() + ")"
                + " ON CONFLICT (id)"
                + " DO UPDATE SET"
                + " " + helper.getUpdateDescription();

        connection.getConnection().ifPresent(conn -> {
            try (var st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                userRepoQuery.setStatementValues(st, helper.getValues());
                st.executeUpdate();
                var rs = st.getGeneratedKeys();
                var hasNext = rs.next();
                var id = rs.getInt("id");
                idRef.set(id);
            } catch (final SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        return user.toBuilder().id(idRef.get()).build();
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
}
