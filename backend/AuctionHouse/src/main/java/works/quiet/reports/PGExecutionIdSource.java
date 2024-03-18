package works.quiet.reports;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.IdSource;

import java.util.logging.Level;

@Log
public class PGExecutionIdSource implements IdSource<Execution> {
    private final DBInterface dbInterface;

    public PGExecutionIdSource(final Level logLevel, final DBInterface dbInterface) {
        this.dbInterface = dbInterface;
        log.setLevel(logLevel);
    }

    @Override
    public int generateId(final Execution entity) {
        return entity.getId() == 0 ? dbInterface.nextVal("SELECT nextval('executions_id_seq')") : entity.getId();
    }
}
