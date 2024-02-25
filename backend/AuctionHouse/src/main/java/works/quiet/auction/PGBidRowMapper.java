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
                .id(resultSet.getInt(fieldPrefix + "id"))
                .auctionId(resultSet.getInt(fieldPrefix + "auctionId"))
                .amount(resultSet.getBigDecimal(fieldPrefix + "amount"))
                .bidderId(resultSet.getInt(fieldPrefix + "bidderId"))
                .createdAt(resultSet.getTimestamp(fieldPrefix + "createdAt").toInstant())
                .build();
    }
}
