package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.IdSource;

import java.util.logging.Level;

@Log
public class PGUserIdSource implements IdSource<User> {
    private final DBInterface dbInterface;

    public PGUserIdSource(final Level logLevel, final DBInterface dbInterface) {
        this.dbInterface = dbInterface;
        log.setLevel(logLevel);
    }

    @Override
    public int generateId(final User entity) {
        return entity.getId() == 0 ? dbInterface.nextVal("SELECT nextval('users_id_seq')") : entity.getId();
    }
}
