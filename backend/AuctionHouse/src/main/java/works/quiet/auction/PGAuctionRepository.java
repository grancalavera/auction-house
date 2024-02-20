package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.MutationHelper;
import works.quiet.db.PGMapper;
import works.quiet.db.QueryHelper;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGAuctionRepository implements AuctionRepository {
    private final QueryHelper<Auction> queryHelper;
    private final PGMapper<Auction> mapper;
    private final MutationHelper mutationHelper;


    public PGAuctionRepository(
            final Level logLevel,
            final QueryHelper<Auction> queryHelper,
            final PGMapper<Auction> mapper,
            final MutationHelper mutationHelper
    ) {
        this.queryHelper = queryHelper;
        this.mapper = mapper;
        this.mutationHelper = mutationHelper;
        log.setLevel(logLevel);
    }

    @Override
    public Optional<Auction> findById(final int id) {
        return Optional.empty();
    }

    @Override
    public List<Auction> findAll() {
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
    public Auction save(final Auction entity) {
        var id = mutationHelper.save(
                entity.getId() == 0,
                new String[]{
                        "id",
                        "seller_id",
                        "symbol",
                        "quantity",
                        "price",
                        "status_id",
                },
                new Object[]{
                        entity.getId(),
                        entity.getSellerId(),
                        entity.getSymbol(),
                        entity.getQuantity(),
                        entity.getPrice(),
                        entity.getStatus().getId()
                });

        return entity.toBuilder().id(id).build();
    }

    @Override
    public void delete(final Auction entity) {

    }

    @Override
    public List<Auction> listAuctionsBySellerId(final int sellerId) {
        return queryHelper.queryMany(conn -> {
                    var st = conn.prepareStatement("SELECT * FROM auctions WHERE seller_id=?");
                    st.setInt(1, sellerId);
                    return st;
                },
                mapper::fromResulSet
        );
    }
}
