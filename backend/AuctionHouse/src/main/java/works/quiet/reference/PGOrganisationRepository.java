package works.quiet.reference;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.IdSource;
import works.quiet.db.PGMapper;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGOrganisationRepository implements OrganisationRepository {
    private final DBInterface dbInterface;
    private final PGMapper<Organisation> rowMapper;
    private final IdSource<Organisation> idSource;


    public PGOrganisationRepository(
            final Level logLevel,
            final DBInterface dbInterface,
            final PGMapper<Organisation> rowMapper,
            final IdSource<Organisation> idSource
    ) {
        this.dbInterface = dbInterface;
        this.rowMapper = rowMapper;
        this.idSource = idSource;
        log.setLevel(logLevel);
    }

    @Override
    public Optional<Organisation> findById(final int id) {
        return dbInterface.queryOne(
                rowMapper::fromResulSet,
                "SELECT * from organisations WHERE id=? LIMIT 1",
                id
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
                rowMapper::fromResulSet,
                "SELECT * from organisations WHERE name=?",
                name
        );
    }

    @Override
    public List<Organisation> findAll() {
        return dbInterface.queryMany(rowMapper::fromResulSet, "SELECT * FROM organisations");
    }

    @Override
    public long count() {
        return dbInterface.queryCount("SELECT count(id) from organisations");
    }

    @Override
    public boolean exists(final int id) {
        return dbInterface.queryExists("SELECT id FROM organisations WHERE id=?", id);
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
