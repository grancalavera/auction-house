package works.quiet.reference;

import lombok.extern.java.Log;
import works.quiet.db.PGMapper;
import works.quiet.db.RepositoryQuery;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGOrganisationRepository implements OrganisationRepository {
    private final RepositoryQuery<Organisation> orgRepoQuery;
    private final PGMapper<Organisation> mapper;


    public PGOrganisationRepository(
            final Level logLevel,
            final RepositoryQuery<Organisation> orgRepoQuery,
            final PGMapper<Organisation> mapper
    ) {
        this.orgRepoQuery = orgRepoQuery;
        this.mapper = mapper;
        log.setLevel(logLevel);
    }

    @Override
    public Optional<Organisation> findOne(final int id) {
        return orgRepoQuery.queryOne(conn -> {
            var st = conn.prepareStatement("SELECT * from organisations WHERE id=?");
            st.setInt(1, id);
            return st;
        }, mapper::fromResulSet);
    }

    /**
     * Case-insensitive organisation search.
     * @param name The case-insensitive organisation name.
     * @return Optional<Organisation>
     */
    @Override
    public Optional<Organisation> findByName(final String name) {
        return orgRepoQuery.queryOne(conn -> {
            var st = conn.prepareStatement("SELECT * from organisations WHERE name=?");
            st.setString(1, name);
            return st;
        }, mapper::fromResulSet);
    }

    @Override
    public List<Organisation> findAll() {
        return orgRepoQuery.queryMany(conn ->
                conn.prepareStatement("SELECT * FROM organisations"),
                mapper::fromResulSet
        );
    }

    @Override
    public long count() {
        return orgRepoQuery.queryCount(
                conn -> conn.prepareStatement("SELECT count(id) from organisations")
        );
    }

    @Override
    public boolean exists(final int id) {
        return orgRepoQuery.queryExists(conn -> {
            var st = conn.prepareStatement("SELECT id FROM organisations WHERE id=?");
            st.setInt(1, id);
            return st;
        });
    }

    @Override
    public Organisation save(final Organisation entity) throws Exception {
        throw new Exception("Not Implemented");
    }

    @Override
    public void delete(final Organisation entity) throws Exception {
        throw new Exception("Not Implemented");
    }
}
