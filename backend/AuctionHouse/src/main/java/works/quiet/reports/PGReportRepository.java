package works.quiet.reports;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.IdSource;
import works.quiet.db.PGMapper;
import works.quiet.resources.Resources;

import java.sql.Timestamp;
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
                        + "(id, auctionid, revenue, soldquantity, createdat, sellerid) "
                        + "values (?, ?, ?, ?, ?, ?)"
                        + "ON CONFLICT (id) DO UPDATE SET "
                        + "auctionid = excluded.auctionid,"
                        + "revenue = excluded.revenue,"
                        + "soldquantity = excluded.soldquantity,"
                        + "createdAt = excluded.createdAt",

                idSource.generateId(entity),
                entity.getAuctionId(),
                entity.getRevenue(),
                entity.getSoldQuantity(),
                Timestamp.from(entity.getCreatedAt()),
                entity.getSellerId()
        );

        return entity.toBuilder().id(id).build();
    }

    @Override
    public void delete(final Report entity) {

    }

    @Override
    public List<Report> findAllBySellerId(final int sellerId) {
        return dbInterface.rawQuery(
                reportRawQueryMapper::fromResulSet,
                "SELECT "
                        + "report.id,"
                        + "report.auctionId,"
                        + "report.revenue,"
                        + "report.soldQuantity,"
                        + "report.createdAt,"
                        + "report.sellerid,"

                        + "execution.id as execution_id,"
                        + "execution.auctionId as execution_auctionId,"
                        + "execution.bidId as execution_bidId,"
                        + "execution.bidderId as execution_bidderId,"
                        + "execution.filledQuantity as execution_filledQuantity "

                        + "FROM reports report "
                        + "JOIN executions execution on execution.auctionId = report.auctionId "
                        + "WHERE report.sellerid = ?",
                sellerId
        );
    }

    @Override
    public boolean existsByAuctionId(final int auctionId) {
        return dbInterface.queryExists("SELECT id from reports WHERE auctionId=?", auctionId);
    }
}
