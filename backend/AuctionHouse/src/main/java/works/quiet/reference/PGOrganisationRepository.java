package works.quiet.reference;

import lombok.extern.java.Log;
import works.quiet.db.PGRowMapper;
import works.quiet.db.QueryHelper;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGOrganisationRepository implements OrganisationRepository {
    private final QueryHelper queryHelper;
    private final PGRowMapper<Organisation> mapper;


    public PGOrganisationRepository(
            final Level logLevel,
            final QueryHelper queryHelper,
            final PGRowMapper<Organisation> mapper
    ) {
        this.queryHelper = queryHelper;
        this.mapper = mapper;
        log.setLevel(logLevel);
    }

    @Override
    public Optional<Organisation> findById(final int id) {
        return queryHelper.queryOne(conn -> {
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
        return queryHelper.queryOne(conn -> {
            var st = conn.prepareStatement("SELECT * from organisations WHERE name=?");
            st.setString(1, name);
            return st;
        }, mapper::fromResulSet);
    }

    @Override
    public List<Organisation> findAll() {
        return queryHelper.queryMany(conn ->
                        conn.prepareStatement("SELECT * FROM organisations"),
                mapper::fromResulSet
        );
    }

    @Override
    public long count() {
        return queryHelper.queryCount(
                conn -> conn.prepareStatement("SELECT count(id) from organisations")
        );
    }

    @Override
    public boolean exists(final int id) {
        return queryHelper.queryExists(conn -> {
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
