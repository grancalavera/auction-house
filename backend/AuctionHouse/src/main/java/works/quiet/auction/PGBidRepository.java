package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.IdSource;
import works.quiet.db.PGMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGBidRepository implements BidRepository {

    private final DBInterface dbInterface;
    private final PGMapper<Integer> upsertMapper;
    private final IdSource<Bid> idSource;

    private final PGMapper<Bid> rowMapper;

    public PGBidRepository(
            final Level logLevel,
            final DBInterface dbInterface,
            final PGMapper<Integer> upsertMapper,
            final IdSource<Bid> idSource,
            final PGMapper<Bid> rowMapper
    ) {
        this.upsertMapper = upsertMapper;
        this.idSource = idSource;
        this.rowMapper = rowMapper;
        log.setLevel(logLevel);
        this.dbInterface = dbInterface;
    }

    @Override
    public Optional<Bid> findById(final int id) {
        return Optional.empty();
    }

    @Override
    public List<Bid> findAll() {
        return null;
    }

    @Override
    public long count() {
        return dbInterface.queryCount("SELECT count(id) from bids");
    }

    @Override
    public boolean exists(final int id) {
        return false;
    }

    @Override
    public Bid save(final Bid entity) {
        var id = dbInterface.upsert(
                upsertMapper::fromResulSet,

                "INSERT INTO bids"
                        + "(id, bidderId, auctionId, amount, createdAt)"
                        + "values (?, ?, ?, ?, ?)"
                        + "ON CONFLICT (id) DO UPDATE SET "
                        + "bidderId = excluded.bidderId,"
                        + "auctionId = excluded.auctionId,"
                        + "amount = excluded.amount,"
                        + "createdAt = excluded.createdAt",

                idSource.generateId(entity),
                entity.getBidderId(),
                entity.getAuctionId(),
                entity.getAmount(),
                Timestamp.from(entity.getCreatedAt())
        );

        return entity.toBuilder().id(id).build();
    }

    @Override
    public void delete(final Bid entity) {
    }

    @Override
    public List<Bid> findAllByBidderId(final int bidderId) {
        return dbInterface.queryMany(
                rowMapper::fromResulSet,
                "SELECT * from bids WHERE bidderid=?",
                bidderId
        );
    }
}
