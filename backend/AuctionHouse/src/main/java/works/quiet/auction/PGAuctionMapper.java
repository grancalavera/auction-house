package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.PGRowMapper;

import java.sql.ResultSet;
import java.util.logging.Level;

@Log
public class PGAuctionMapper implements PGRowMapper<Auction> {

    public PGAuctionMapper(final Level logLevel) {
        log.setLevel(logLevel);
    }

    @Override
    public Auction fromResulSet(final ResultSet resultSet) throws Exception {
        var status = AuctionStatus.ofInt(resultSet.getInt("status_id"));

        return Auction.builder()
                .id(resultSet.getInt("id"))
                .symbol(resultSet.getString("symbol"))
                .quantity(resultSet.getInt("quantity"))
                .price(resultSet.getBigDecimal("price"))
                .sellerId(resultSet.getInt("seller_id"))
                .status(status)
                .build();
    }
}
