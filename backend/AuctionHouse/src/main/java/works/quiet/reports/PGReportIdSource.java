package works.quiet.reports;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.IdSource;

import java.util.logging.Level;

@Log
public class PGReportIdSource implements IdSource<Report> {
    private final DBInterface dbInterface;

    public PGReportIdSource(final Level logLevel, final DBInterface dbInterface) {
        this.dbInterface = dbInterface;
        log.setLevel(logLevel);
    }

    @Override
    public int generateId(final Report entity) {
        return entity.getId() == 0 ? dbInterface.nextVal("SELECT nextval('reports_id_seq')") : entity.getId();
    }
}
