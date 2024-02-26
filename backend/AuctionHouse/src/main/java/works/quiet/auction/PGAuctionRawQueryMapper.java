package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.PGMapper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Log
public class PGAuctionRawQueryMapper implements PGMapper<List<Auction>> {
    private final PGMapper<Auction> auctionRowMapper;
    private final PGMapper<Bid> bidRowMapper;

    public PGAuctionRawQueryMapper(
            final Level logLevel,
            final PGMapper<Auction> auctionRowMapper,
            final PGMapper<Bid> bidRowMapper
    ) {
        this.auctionRowMapper = auctionRowMapper;
        this.bidRowMapper = bidRowMapper;
        log.setLevel(logLevel);
    }

    @Override
    public List<Auction> fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public List<Auction> fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {
        // A map and a list yes... a list to keep the order of the query, and a map to
        // find auctions quickly and add more bids to them as the ResultSet is mapped.
        Map<Integer, Auction> index = new HashMap<>();
        ArrayList<Auction> result = new ArrayList<>();

        while (resultSet.next()) {
            var auctionId = resultSet.getInt("id");
            Auction row;

            if (index.containsKey(auctionId)) {
                row = index.get(auctionId);
            } else {
                row = auctionRowMapper.fromResulSet(resultSet);
                result.add(row);
                index.put(auctionId, row);
            }

            // all bid-related columns use the "bid_" prefix
            if (resultSet.getInt("bid_id") == 0) {
                break;
            }

            var bid = bidRowMapper.fromResulSet("bid_", resultSet);
            row.getBids().add(bid);
        }

        return result;
    }
}
