package works.quiet.reference;

import lombok.extern.java.Log;
import works.quiet.db.PGMapper;

import java.sql.ResultSet;
import java.util.logging.Level;

@Log
public class PGOrganisationMapper implements PGMapper<Organisation> {

    public PGOrganisationMapper(final Level logLevel) {
        log.setLevel(logLevel);
    }

    @Override
    public Organisation fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public Organisation fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {
        return Organisation
                .builder()
                .id(resultSet.getInt(fieldPrefix + "id"))
                .name(resultSet.getString(fieldPrefix + "name"))
                .build();
    }
}
