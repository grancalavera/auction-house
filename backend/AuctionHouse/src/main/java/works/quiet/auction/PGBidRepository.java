package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.PGMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGBidRepository implements BidRepository {

    private final DBInterface dbInterface;
    private final PGMapper<Integer> upsertMapper;

    public PGBidRepository(
            final Level logLevel,
            final DBInterface dbInterface,
            final PGMapper<Integer> upsertMapper
    ) {
        this.upsertMapper = upsertMapper;
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

                generateId(entity),
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
    public int generateId(final Bid entity) {
        return entity.getId() == 0 ? dbInterface.nextVal("SELECT nextval('bids_id_seq')") : entity.getId();
    }
}
