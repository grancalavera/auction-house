package works.quiet.reference;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.PGMapper;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGOrganisationRepository implements OrganisationRepository {
    private final DBInterface dbInterface;
    private final PGMapper<Organisation> mapper;


    public PGOrganisationRepository(
            final Level logLevel,
            final DBInterface dbInterface,
            final PGMapper<Organisation> mapper
    ) {
        this.dbInterface = dbInterface;
        this.mapper = mapper;
        log.setLevel(logLevel);
    }

    @Override
    public Optional<Organisation> findById(final int id) {
        return dbInterface.queryOne(conn -> {
            var st = conn.prepareStatement("SELECT * from organisations WHERE id=? LIMIT 1");
            st.setInt(1, id);
            return st;
        }, mapper::fromResulSet);
    }

    /**
     * Case-insensitive organisation search.
     *
     * @param name The case-insensitive organisation name.
     * @return Optional<Organisation>
     */
    @Override
    public Optional<Organisation> findByName(final String name) {
        return dbInterface.queryOne(conn -> {
            var st = conn.prepareStatement("SELECT * from organisations WHERE name=?");
            st.setString(1, name);
            return st;
        }, mapper::fromResulSet);
    }

    @Override
    public List<Organisation> findAll() {
        return dbInterface.queryMany(conn ->
                        conn.prepareStatement("SELECT * FROM organisations"),
                mapper::fromResulSet
        );
    }

    @Override
    public long count() {
        return dbInterface.queryCount(
                conn -> conn.prepareStatement("SELECT count(id) from organisations")
        );
    }

    @Override
    public boolean exists(final int id) {
        return dbInterface.queryExists(conn -> {
            var st = conn.prepareStatement("SELECT id FROM organisations WHERE id=?");
            st.setInt(1, id);
            return st;
        });
    }

    @Override
    public Organisation save(final Organisation entity) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void delete(final Organisation entity) {
        throw new RuntimeException("Not Implemented");
    }
}
