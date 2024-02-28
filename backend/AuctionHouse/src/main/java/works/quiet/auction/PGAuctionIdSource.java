package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.IdSource;

import java.util.logging.Level;

@Log
public class PGAuctionIdSource implements IdSource<Auction> {

    private final DBInterface dbInterface;

    public PGAuctionIdSource(final Level logLevel, final DBInterface dbInterface) {
        this.dbInterface = dbInterface;
        log.setLevel(logLevel);
    }

    @Override
    public int generateId(final Auction entity) {
        return entity.getId() == 0 ? dbInterface.nextVal("SELECT nextval('auctions_id_seq')") : entity.getId();
    }
}
