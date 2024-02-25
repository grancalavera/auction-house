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
        Map<Integer, Auction> result = new HashMap<>();

        while (resultSet.next()) {
            var auctionId = resultSet.getInt("id");
            Auction row;

            if (result.containsKey(auctionId)) {
                row = result.get(auctionId);
            } else {
                row = auctionRowMapper.fromResulSet(resultSet);
                result.put(auctionId, row);
            }

            // all bid-related columns use the "bid_" prefix
            if (resultSet.getInt("bid_id") == 0) {
                break;
            }

            var bid = bidRowMapper.fromResulSet("bid_", resultSet);
            row.getBids().add(bid);
        }

        return new ArrayList<>(result.values());
    }
}
