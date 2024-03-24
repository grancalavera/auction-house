package works.quiet.reports;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.IdSource;
import works.quiet.db.PGMapper;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
  public class PGExecutionRepository implements ExecutionRepository {

    private final DBInterface dbInterface;
    private final PGMapper<Integer> upsertMapper;
    private final IdSource<Execution> idSource;
    private final PGMapper<Execution> rowMapper;

    public PGExecutionRepository(
            final Level logLevel,
            final DBInterface dbInterface,
            final PGMapper<Integer> upsertMapper,
            final IdSource<Execution> idSource,
            final PGMapper<Execution> rowMapper
    ) {
        this.dbInterface = dbInterface;
        this.upsertMapper = upsertMapper;
        this.idSource = idSource;
        this.rowMapper = rowMapper;
    }

    @Override
    public Optional<Execution> findById(final int id) {
        return Optional.empty();
    }

    @Override
    public List<Execution> findAll() {
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
    public Execution save(final Execution entity) {
        var id = dbInterface.upsert(
                upsertMapper::fromResulSet,
                "INSERT INTO executions"
                        + "(id, auctionid, bidid, bidderid, filledquantity) "
                        + "values (?, ?, ?, ?, ?)"
                        + "ON CONFLICT (id) DO UPDATE SET "
                        + "auctionid = excluded.auctionid,"
                        + "bidid = excluded.bidid,"
                        + "filledquantity = excluded.filledquantity",
                idSource.generateId(entity),
                entity.getAuctionId(),
                entity.getBidId(),
                entity.getBidderId(),
                entity.getFilledQuantity()
        );

        return entity.toBuilder().id(id).build();
    }

    @Override
    public void delete(final Execution entity) {

    }

    @Override
    public List<Execution> findAllByBidderId(final int bidderId) {
        return dbInterface.queryMany(
                rowMapper::fromResulSet,
                "SELECT * FROM  executions WHERE bidderid=?",
                bidderId
        );
    }
}
