package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGBidRepository implements BidRepository {

    private final DBInterface dbInterface;

    public PGBidRepository(
            final Level logLevel,
            final DBInterface dbInterface
    ) {
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
        return dbInterface.queryCount(conn -> conn.prepareStatement("SELECT count(id) from bids"));
    }

    @Override
    public boolean exists(final int id) {
        return false;
    }

    @Override
    public Bid save(final Bid entity) {
        var id = dbInterface.upsert(
                "bids",
                entity.getId() == 0,
                new String[]{
                        "id",
                        "bidderId",
                        "auctionId",
                        "amount",
                        "createdAt",
                },
                new Object[]{
                        entity.getId(),
                        entity.getBidderId(),
                        entity.getAuctionId(),
                        entity.getAmount(),
                        Timestamp.from(entity.getCreatedAt())
                }
        );
        return entity.toBuilder().id(id).build();
    }

    @Override
    public void delete(final Bid entity) {

    }
}
