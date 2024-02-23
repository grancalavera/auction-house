package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.MutationHelper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGBidRepository implements BidRepository {

    private final MutationHelper mutationHelper;

    public PGBidRepository(
            final Level logLevel,
            final MutationHelper mutationHelper
    ) {
        log.setLevel(logLevel);
        this.mutationHelper = mutationHelper;
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
        return 0;
    }

    @Override
    public boolean exists(final int id) {
        return false;
    }
    @Override
    public Bid save(final Bid entity) {
        var id = mutationHelper.save(
                entity.getId() == 0,
                new String[]{
                        "id",
                        "bidder_id",
                        "auction_id",
                        "amount",
                        "bidTimestamp",
                },
                new Object[]{
                        entity.getId(),
                        entity.getBidderId(),
                        entity.getAuctionId(),
                        entity.getAmount(),
                        Timestamp.from(entity.getBidTimestamp())
                }
        );
        return entity.toBuilder().id(id).build();
    }

    @Override
    public void delete(final Bid entity) {

    }
}
