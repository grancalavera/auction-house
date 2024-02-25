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
    private final PGMapper<Organisation> rowMapper;


    public PGOrganisationRepository(
            final Level logLevel,
            final DBInterface dbInterface,
            final PGMapper<Organisation> rowMapper
    ) {
        this.dbInterface = dbInterface;
        this.rowMapper = rowMapper;
        log.setLevel(logLevel);
    }

    @Override
    public Optional<Organisation> findById(final int id) {
        return dbInterface.queryOne(
                "SELECT * from organisations WHERE id=? LIMIT 1",
                new Object[]{id},
                rowMapper::fromResulSet
        );
    }

    /**
     * Case-insensitive organisation search.
     *
     * @param name The case-insensitive organisation name.
     * @return Optional<Organisation>
     */
    @Override
    public Optional<Organisation> findByName(final String name) {
        return dbInterface.queryOne(
                "SELECT * from organisations WHERE name=?",
                new Object[]{name},
                rowMapper::fromResulSet
        );
    }

    @Override
    public List<Organisation> findAll() {
        return dbInterface.queryMany("SELECT * FROM organisations", rowMapper::fromResulSet);
    }

    @Override
    public long count() {
        return dbInterface.queryCount("SELECT count(id) from organisations");
    }

    @Override
    public boolean exists(final int id) {
        return dbInterface.queryExists("SELECT id FROM organisations WHERE id=?", new Object[]{id});
    }

    @Override
    public Organisation save(final Organisation entity) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void delete(final Organisation entity) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public int nextId() {
        return 0;
    }
}
