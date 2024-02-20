package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.db.MutationHelper;
import works.quiet.db.PGMapper;
import works.quiet.db.QueryHelper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGUserRepository implements UserRepository {
    private final QueryHelper<User> queryHelper;
    private final MutationHelper mutationHelper;
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
            final Level logLevel, final QueryHelper<User> queryHelper,
            final PGMapper<User> mapper, final MutationHelper mutationHelper) {
        this.queryHelper = queryHelper;
        this.mutationHelper = mutationHelper;
        this.mapper = mapper;
        log.setLevel(logLevel);
    }

    @Override
    public List<User> findAll() {
        return queryHelper.queryMany(
                (conn) -> conn.prepareStatement(usersQuery + " ORDER BY id"),
                mapper::fromResulSet
        );
    }

    @Override
    public long count() {
        return queryHelper.queryCount(conn -> conn.prepareStatement("SELECT count(id) FROM users"));
    }

    @Override
    public boolean exists(final int id) {
        return queryHelper.queryExists(conn -> {
            var st = conn.prepareStatement("SELECT id FROM users WHERE id=?");
            st.setInt(1, id);
            return st;
        });
    }

    @Override
    public Optional<User> findWithCredentials(final String username, final String password) {
        return queryHelper.queryOne(
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
        return queryHelper.queryOne(
                (conn) -> {
                    var st = conn.prepareStatement(usersQuery + " WHERE u.username=?"
                    );
                    st.setString(1, username);
                    return st;
                },
                mapper::fromResulSet
        );
    }

    @Override
    public Optional<User> findById(final int id) {
        return queryHelper.queryOne(
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
    public User save(final User entity) {
        var id = mutationHelper.save(
                entity.getId() == 0,
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
        mutationHelper.delete(user.getId());
    }
}
