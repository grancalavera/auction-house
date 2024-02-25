package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.PGRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGUserRepository implements UserRepository {
    private final DBInterface dbInterface;
    private final PGRowMapper<User> rowMapper;

    private final String usersQuery = "SELECT"
            + " u.id,"
            + " u.username,"
            + " u.password,"
            + " u.firstname,"
            + " u.lastname,"
            + " a.name as accountStatus,"
            + " r.name as role,"
            + " u.organisationId as organisationId,"
            + " o.name as organisation"
            + " FROM users u"
            + " LEFT JOIN organisations o on u.organisationId = o.id"
            + " LEFT JOIN accountStatus a on u.accountstatusId = a.id"
            + " LEFT JOIN roles r on u.roleid = r.id";

    public PGUserRepository(
            final Level logLevel,
            final DBInterface dbInterface,
            final PGRowMapper<User> rowMapper
    ) {
        this.dbInterface = dbInterface;
        this.rowMapper = rowMapper;
        log.setLevel(logLevel);
    }

    @Override
    public List<User> findAll() {
        return dbInterface.queryMany(
                (conn) -> conn.prepareStatement(usersQuery + " ORDER BY id"),
                rowMapper::fromResulSet
        );
    }

    @Override
    public long count() {
        return dbInterface.queryCount(conn -> conn.prepareStatement("SELECT count(id) FROM users"));
    }

    @Override
    public boolean exists(final int id) {
        return dbInterface.queryExists(conn -> {
            var st = conn.prepareStatement("SELECT id FROM users WHERE id=?");
            st.setInt(1, id);
            return st;
        });
    }

    @Override
    public Optional<User> findWithCredentials(final String username, final String password) {
        return dbInterface.queryOne(
                (conn) -> {
                    PreparedStatement st = conn.prepareStatement(
                            usersQuery + " WHERE u.username=? AND u.password=?"
                    );
                    st.setString(1, username);
                    st.setString(2, password);
                    return st;
                },
                rowMapper::fromResulSet
        );
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        return dbInterface.queryOne(
                (conn) -> {
                    var st = conn.prepareStatement(usersQuery + " WHERE u.username=?"
                    );
                    st.setString(1, username);
                    return st;
                },
                rowMapper::fromResulSet
        );
    }

    @Override
    public Optional<User> findById(final int id) {
        return dbInterface.queryOne(
                (conn) -> {
                    var st = conn.prepareStatement(usersQuery + " WHERE u.id=?"
                    );
                    st.setInt(1, id);
                    return st;
                },
                rowMapper::fromResulSet
        );
    }

    @Override
    public User save(final User entity) {
        var id = dbInterface.upsert(
                "users",
                entity.getId() == 0,
                new String[]{
                        "id",
                        "username",
                        "password",
                        "firstName",
                        "lastName",
                        "organisationId",
                        "roleId",
                        "accountStatusId"
                },
                new Object[]{
                        entity.getId(),
                        entity.getUsername(),
                        entity.getPassword(),
                        entity.getFirstName(),
                        entity.getLastName(),
                        entity.getOrganisation().getId(),
                        entity.getAccountStatus().getId(),
                        entity.getRole().getId()
                });

        return entity.toBuilder().id(id).build();
    }

    @Override
    public void delete(final User user) {
        dbInterface.delete("users", user.getId());
    }
}
