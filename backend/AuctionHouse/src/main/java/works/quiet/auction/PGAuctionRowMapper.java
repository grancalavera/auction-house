package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.PGMapper;

import java.sql.ResultSet;
import java.util.logging.Level;

@Log
public class PGAuctionRowMapper implements PGMapper<Auction> {

    public PGAuctionRowMapper(final Level logLevel) {
        log.setLevel(logLevel);
    }

    @Override
    public Auction fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public Auction fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {

        var closedAt = resultSet.getTimestamp(fieldPrefix + "closedAt");

        return Auction.builder()
                .id(resultSet.getInt(fieldPrefix + "id"))
                .symbol(resultSet.getString(fieldPrefix + "symbol"))
                .quantity(resultSet.getInt(fieldPrefix + "quantity"))
                .price(resultSet.getBigDecimal(fieldPrefix + "price"))
                .sellerId(resultSet.getInt(fieldPrefix + "sellerId"))
                .createdAt(resultSet.getTimestamp(fieldPrefix + "createdAt").toInstant())
                .closedAt(closedAt == null ? null : closedAt.toInstant())
                .build();
    }
}
