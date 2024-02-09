package works.quiet.reference;

import works.quiet.db.DBConnection;
import works.quiet.db.PGRepositoryQuery;

import java.util.logging.Level;

public class PGOrganisationRepositoryQuery extends PGRepositoryQuery<Organisation> {
    public PGOrganisationRepositoryQuery(final Level logLevel, final DBConnection connection) {
        super(logLevel, connection);
    }
}
