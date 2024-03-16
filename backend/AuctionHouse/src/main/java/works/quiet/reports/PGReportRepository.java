package works.quiet.reports;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.IdSource;
import works.quiet.db.PGMapper;
import works.quiet.resources.Resources;
import works.quiet.user.User;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGReportRepository implements ReportRepository {

    private final Resources resources;
    private final DBInterface dbInterface;

    private final PGMapper<Integer> upsertMapper;
    private final IdSource<Report> idSource;
    private final PGMapper<List<Report>> reportRawQueryMapper;

    public PGReportRepository(
            final Level logLevel,
            final Resources resources,
            final DBInterface dbInterface,
            final IdSource<Report> idSource,
            final PGMapper<Integer> upsertMapper,
            final PGMapper<List<Report>> reportRawQueryMapper
    ) {
        this.dbInterface = dbInterface;
        this.upsertMapper = upsertMapper;
        this.idSource = idSource;
        this.reportRawQueryMapper = reportRawQueryMapper;
        log.setLevel(logLevel);
        this.resources = resources;
    }

    @Override
    public Optional<Report> findById(final int id) {
        return Optional.empty();
    }

    @Override
    public List<Report> findAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public boolean exists(final int id) {
        return false;
    }

    @Override
    public Report save(final Report entity) {
        var id = dbInterface.upsert(
                upsertMapper::fromResulSet,

                "INSERT INTO reports"
                        + "(id, auctionid, revenue, soldquantity) "
                        + "values (?, ?, ?, ?)"
                        + "ON CONFLICT (id) DO UPDATE SET "
                        + "auctionid = excluded.auctionid,"
                        + "revenue = excluded.revenue,"
                        + "soldquantity = excluded.soldquantity",

                idSource.generateId(entity),
                entity.getAuctionId(),
                entity.getRevenue(),
                entity.getSoldQuantity()
        );

        return entity.toBuilder().id(id).build();
    }

    @Override
    public void delete(final Report entity) {

    }

    @Override
    public List<Report> findReportsForUser(final User user) {
        return null;
    }
}
