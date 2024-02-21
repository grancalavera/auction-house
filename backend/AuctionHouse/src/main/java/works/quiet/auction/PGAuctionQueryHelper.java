package works.quiet.auction;

import works.quiet.db.DBConnection;
import works.quiet.db.PGQueryHelper;

import java.util.logging.Level;

public class PGAuctionQueryHelper extends PGQueryHelper<Auction> {
    public PGAuctionQueryHelper(final Level logLevel, final DBConnection connection) {
        super(logLevel, connection);
    }
}
