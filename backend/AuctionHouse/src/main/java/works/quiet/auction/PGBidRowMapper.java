package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.PGRowMapper;

import java.sql.ResultSet;
import java.util.logging.Level;

@Log
public class PGBidRowMapper implements PGRowMapper<Bid> {

    public PGBidRowMapper(final Level logLevel) {
        log.setLevel(logLevel);
    }

    @Override
    public Bid fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public Bid fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {
        return Bid.builder()
                .id(resultSet.getInt(fieldPrefix + "bid_id"))
                .auctionId(resultSet.getInt(fieldPrefix + "bid_auction_id"))
                .amount(resultSet.getBigDecimal(fieldPrefix + "bid_amount"))
                .bidderId(resultSet.getInt(fieldPrefix + "bid_bidder_id"))
                .bidTimestamp(resultSet.getTimestamp(fieldPrefix + "bid_bidtimestamp").toInstant())
                .build();
    }
}
