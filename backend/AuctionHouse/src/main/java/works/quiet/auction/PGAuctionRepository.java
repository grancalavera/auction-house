package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.PGMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGAuctionRepository implements AuctionRepository {
    private final DBInterface dbInterface;
    private final PGMapper<List<Auction>> auctionRawQueryMapper;
    private final String auctionsQuery = "SELECT"
            + " auction.id,"
            + " auction.sellerId,"
            + " auction.symbol,"
            + " auction.quantity,"
            + " auction.price,"
            + " auction.statusId,"
            + " auction.createdAt,"
            + " auction.closedAt,"
            + " bid.id as bid_id,"
            + " bid.auctionId as bid_auctionId,"
            + " bid.bidderId as bid_bidderId,"
            + " bid.amount as bid_amount,"
            + " bid.createdAt as bid_createdAt"
            + " FROM auctions auction"
            + " LEFT JOIN bids bid ON bid.auctionId = auction.id";

    public PGAuctionRepository(
            final Level logLevel,
            final DBInterface dbInterface,
            final PGMapper<List<Auction>> auctionRawQueryMapper
    ) {
        this.dbInterface = dbInterface;
        this.auctionRawQueryMapper = auctionRawQueryMapper;
        log.setLevel(logLevel);
    }

    @Override
    public Optional<Auction> findById(final int id) {
        return dbInterface.rawQuery(conn -> {
            var st = conn.prepareStatement(auctionsQuery + " WHERE auction.id=? LIMIT 1");
            st.setInt(1, id);
            return st;
        }, rs -> {
            var result = auctionRawQueryMapper.fromResulSet(rs);
            return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
        });
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
        var closedAt = entity.getClosedAt();
        var id = dbInterface.upsert(
                "auctions",
                entity.getId() == 0,
                new String[]{
                        "id",
                        "sellerId",
                        "symbol",
                        "quantity",
                        "price",
                        "statusId",
                        "createdAt",
                        "closedAt"
                },
                new Object[]{
                        entity.getId(),
                        entity.getSellerId(),
                        entity.getSymbol(),
                        entity.getQuantity(),
                        entity.getPrice(),
                        entity.getStatus().getId(),
                        Timestamp.from(entity.getCreatedAt()),
                        closedAt == null ? null : Timestamp.from(entity.getClosedAt())
                });

        return entity.toBuilder().id(id).build();
    }

    @Override
    public void delete(final Auction entity) {

    }

    @Override
    public List<Auction> listAuctionsBySellerId(final int sellerId) {
        return dbInterface.rawQuery(conn -> {
                    var st = conn.prepareStatement(auctionsQuery + " WHERE auction.sellerId=?");
                    st.setInt(1, sellerId);
                    return st;
                },
                auctionRawQueryMapper::fromResulSet
        );
    }

    @Override
    public List<Auction> listOpenAuctionsForBidderId(final int bidderId) {
        return dbInterface.rawQuery(conn -> {
                    var st = conn.prepareStatement(
                            // the comparison with NULL is "require" because an illegal state is representable:
                            // status can be CLOSED and the auction can have a closedAt timestamp.
                            auctionsQuery + " WHERE sellerId!=? AND statusId=? AND closedAt IS NULL"
                    );
                    st.setInt(1, bidderId);
                    st.setInt(2, AuctionStatus.OPEN.getId());
                    return st;
                },
                auctionRawQueryMapper::fromResulSet
        );
    }

    @Override
    public Optional<Auction> findAuctionBySellerIdAndAuctionId(final int sellerId, final int auctionId) {
        return dbInterface.rawQuery(conn -> {
                    var st = conn.prepareStatement("SELECT * FROM auctions WHERE sellerId=? AND id=?");
                    st.setInt(1, sellerId);
                    st.setInt(2, auctionId);
                    return st;
                },
                rs -> {
                    var result = auctionRawQueryMapper.fromResulSet(rs);
                    return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
                }
        );
    }
}
