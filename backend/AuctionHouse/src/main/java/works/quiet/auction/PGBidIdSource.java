package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.IdSource;

import java.util.logging.Level;

@Log
public class PGBidIdSource implements IdSource<Bid> {
    private final DBInterface  dbInterface;

    public PGBidIdSource(final Level logLevel, final DBInterface dbInterface) {
        this.dbInterface = dbInterface;
        log.setLevel(logLevel);
    }

    @Override
    public int generateId(final Bid entity) {
        return entity.getId() == 0 ? dbInterface.nextVal("SELECT nextval('bids_id_seq')") : entity.getId();
    }
}
