package works.quiet.reference;

import works.quiet.db.DBConnection;
import works.quiet.db.PGQueryHelper;

import java.util.logging.Level;

public class PGOrganisationQueryHelper extends PGQueryHelper<Organisation> {
    public PGOrganisationQueryHelper(final Level logLevel, final DBConnection connection) {
        super(logLevel, connection);
    }
}
