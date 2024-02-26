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
    private final PGMapper<Integer> upsertMapper;
    private final String auctionsQuery = "SELECT"
            + " auction.id,"
            + " auction.sellerId,"
            + " auction.symbol,"
            + " auction.quantity,"
            + " auction.price,"
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
            final PGMapper<List<Auction>> auctionRawQueryMapper,
            final PGMapper<Integer> upsertMapper
    ) {
        this.dbInterface = dbInterface;
        this.auctionRawQueryMapper = auctionRawQueryMapper;
        this.upsertMapper = upsertMapper;
        log.setLevel(logLevel);
    }

    @Override
    public Optional<Auction> findById(final int id) {
        return dbInterface.rawQuery(
                rs -> auctionRawQueryMapper.fromResulSet(rs).stream().findFirst(),
                auctionsQuery + " WHERE auction.id=? LIMIT 1",
                id
        );
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
                upsertMapper::fromResulSet,

                "INSERT INTO auctions"
                        + "(id, sellerid, symbol, quantity, price, createdat, closedat)"
                        + "values (?, ?, ?, ?, ?, ?, ?)"
                        + "ON CONFLICT (id) DO UPDATE SET "
                        + "sellerid = excluded.sellerid,"
                        + "symbol = excluded.symbol,"
                        + "quantity = excluded.quantity,"
                        + "price = excluded.price,"
                        + "createdat = excluded.createdat,"
                        + "closedat = excluded.closedat",

                generateId(entity),
                entity.getSellerId(),
                entity.getSymbol(),
                entity.getQuantity(),
                entity.getPrice(),
                Timestamp.from(entity.getCreatedAt()),
                closedAt == null ? null : Timestamp.from(entity.getClosedAt())
        );

        return entity.toBuilder().id(id).build();
    }

    @Override
    public void delete(final Auction entity) {

    }

    @Override
    public int generateId(final Auction entity) {
        return entity.getId() == 0 ? dbInterface.nextVal("SELECT nextval('auctions_id_seq')") : entity.getId();
    }

    @Override
    public List<Auction> listAuctionsBySellerId(final int sellerId) {
        return dbInterface.rawQuery(
                auctionRawQueryMapper::fromResulSet,
                auctionsQuery + " WHERE auction.sellerId=?",
                sellerId
        );
    }

    @Override
    public List<Auction> listOpenAuctionsForBidderId(final int bidderId) {
        return dbInterface.rawQuery(
                auctionRawQueryMapper::fromResulSet,
                auctionsQuery + " WHERE sellerId!=? AND closedAt IS NULL",
                bidderId
        );
    }

    @Override
    public Optional<Auction> findAuctionBySellerIdAndAuctionId(final int sellerId, final int auctionId) {
        return dbInterface.rawQuery(
                rs -> auctionRawQueryMapper.fromResulSet(rs).stream().findFirst(),
                auctionsQuery + " WHERE auction.sellerId=? AND auction.id=?",
                sellerId, auctionId
        );
    }
}
