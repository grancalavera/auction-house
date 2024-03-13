package works.quiet.reports;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.resources.Resources;

import java.util.logging.Level;

@Log
public class ExecutionRepository {
    private final DBInterface dbInterface;
    private final Resources resources;

    public ExecutionRepository(
            final Level logLevel,
            final Resources resources,
            final DBInterface dbInterface
    ) {
        this.dbInterface = dbInterface;
        this.resources = resources;
        log.setLevel(logLevel);
    }

    void saveExecution(final Execution execution) {
        dbInterface.upsert(
                rs -> 0,
                "INSERT INTO executions"
                        + "(auctionid, bidid, bidderid, status) "
                        + "values (?, ?, ? , ?)",
                execution.getAuctionId(),
                execution.getBidId(),
                execution.getBidderId(),
                execution.getStatus().getId()
        );
    }
}
