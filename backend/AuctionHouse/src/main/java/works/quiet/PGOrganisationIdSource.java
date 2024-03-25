package works.quiet;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.IdSource;
import works.quiet.reference.Organisation;

import java.util.logging.Level;

@Log
public class PGOrganisationIdSource implements IdSource<Organisation> {

    private final DBInterface dbInterface;

    public PGOrganisationIdSource(final Level logLevel, final DBInterface dbInterface) {
        this.dbInterface = dbInterface;
        log.setLevel(logLevel);
    }

    @Override
    public int generateId(final Organisation entity) {
        return entity.getId() == 0
                ? dbInterface.nextVal("SELECT nextval('organisations_id_seq')")
                : entity.getId();
    }
}
