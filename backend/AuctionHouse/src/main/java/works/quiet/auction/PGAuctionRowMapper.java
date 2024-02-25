package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.PGRowMapper;

import java.sql.ResultSet;
import java.util.logging.Level;

@Log
public class PGAuctionRowMapper implements PGRowMapper<Auction> {

    public PGAuctionRowMapper(final Level logLevel) {
        log.setLevel(logLevel);
    }

    @Override
    public Auction fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public Auction fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {
        var status = AuctionStatus.ofInt(resultSet.getInt(fieldPrefix + "statusId"));

        var builder = Auction.builder()
                .id(resultSet.getInt(fieldPrefix + "id"))
                .symbol(resultSet.getString(fieldPrefix + "symbol"))
                .quantity(resultSet.getInt(fieldPrefix + "quantity"))
                .price(resultSet.getBigDecimal(fieldPrefix + "price"))
                .sellerId(resultSet.getInt(fieldPrefix + "sellerId"))
                .createdAt(resultSet.getTimestamp(fieldPrefix + "createdAt").toInstant())
                .status(status);

        var closedAt = resultSet.getTimestamp(fieldPrefix + "closedAt");
        if (closedAt != null) {
            builder.closedAt(closedAt.toInstant());
        }

        return builder.build();
    }
}
