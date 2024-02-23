package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.PGRowMapper;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGAuctionRepository implements AuctionRepository {
    private final DBInterface dbInterface;
    private final PGRowMapper<Auction> mapper;


    public PGAuctionRepository(
            final Level logLevel,
            final DBInterface dbInterface,
            final PGRowMapper<Auction> mapper
    ) {
        this.dbInterface = dbInterface;
        this.mapper = mapper;
        log.setLevel(logLevel);
    }

    @Override
    public Optional<Auction> findById(final int id) {
        return dbInterface.queryOne(conn -> {
            var st = conn.prepareStatement("SELECT * from auctions WHERE id=? LIMIT 1");
            st.setInt(1, id);
            return st;
        }, mapper::fromResulSet);
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
        var id = dbInterface.upsert(
                "auctions",
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
        return dbInterface.queryMany(conn -> {
                    var st = conn.prepareStatement("SELECT * FROM auctions WHERE seller_id=?");
                    st.setInt(1, sellerId);
                    return st;
                },
                mapper::fromResulSet
        );
    }

    @Override
    public List<Auction> listOpenAuctionsForBidderId(final int bidderId) {
        return dbInterface.queryMany(conn -> {
                    var st = conn.prepareStatement("SELECT * FROM auctions WHERE seller_id!=? AND status_id=?");
                    st.setInt(1, bidderId);
                    st.setInt(2, AuctionStatus.OPEN.getId());
                    return st;
                },
                mapper::fromResulSet
        );
    }

    @Override
    public Optional<Auction> findAuctionBySellerIdAndAuctionId(final int sellerId, final int auctionId) {
        return dbInterface.queryOne(conn -> {
                    var st = conn.prepareStatement("SELECT * FROM auctions WHERE seller_id=? AND id=?");
                    st.setInt(1, sellerId);
                    st.setInt(2, auctionId);
                    return st;
                },
                mapper::fromResulSet
        );
    }
}
