package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.PGMapper;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGUserRepository implements UserRepository {
    private final DBInterface dbInterface;
    private final PGMapper<User> rowMapper;
    private final PGMapper<Integer> upsertMapper;

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
            final PGMapper<User> rowMapper,
            final PGMapper<Integer> upsertMapper
    ) {
        this.dbInterface = dbInterface;
        this.rowMapper = rowMapper;
        this.upsertMapper = upsertMapper;
        log.setLevel(logLevel);
    }

    @Override
    public List<User> findAll() {
        return dbInterface.queryMany(rowMapper::fromResulSet, usersQuery + " ORDER BY id");
    }

    @Override
    public long count() {
        return dbInterface.queryCount("SELECT count(id) FROM users");
    }

    @Override
    public boolean exists(final int id) {
        return dbInterface.queryExists("SELECT id FROM users WHERE id=?", id);
    }

    @Override
    public Optional<User> findWithCredentials(final String username, final String password) {
        return dbInterface.queryOne(
                rowMapper::fromResulSet,
                usersQuery + " WHERE u.username=? AND u.password=?",
                username, password
        );
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        return dbInterface.queryOne(
                rowMapper::fromResulSet,
                usersQuery + " WHERE u.username=?",
                username
        );
    }

    @Override
    public Optional<User> findById(final int id) {
        return dbInterface.queryOne(
                rowMapper::fromResulSet,
                usersQuery + " WHERE u.id=?",
                id

        );
    }

    @Override
    public User save(final User entity) {
        var id = dbInterface.upsert(
                upsertMapper::fromResulSet,

                "INSERT INTO users"
                        + "(id, username, password, firstName, lastName, organisationid, roleId, accountStatusId)"
                        + "values (?, ?, ?, ?, ?, ?, ?, ?)"
                        + "ON CONFLICT (id) DO UPDATE SET "
                        + "username = excluded.username,"
                        + "password = excluded.password,"
                        + "firstName = excluded.firstName,"
                        + "lastName = excluded.lastName,"
                        + "organisationId = excluded.organisationId,"
                        + "roleId = excluded.roleId,"
                        + "accountStatusId = excluded.accountStatusId",

                generateId(entity),
                entity.getUsername(),
                entity.getPassword(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getOrganisation().getId(),
                entity.getAccountStatus().getId(),
                entity.getRole().getId()
        );

        return entity.toBuilder().id(id).build();
    }

    @Override
    public void delete(final User user) {
        dbInterface.delete("DELETE FROM users WHERE id=?", user.getId());
    }

    @Override
    public int generateId(final User entity) {
        return entity.getId() == 0 ? dbInterface.nextVal("SELECT nextval('users_id_seq')") : entity.getId();
    }
}
