package works.quiet.db;

import lombok.extern.java.Log;

import java.sql.ResultSet;
import java.util.logging.Level;

@Log
public class PGUpsertMapper implements PGMapper<Integer> {

    public PGUpsertMapper(final Level logLevel) {
        log.setLevel(logLevel);
    }

    @Override
    public Integer fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public Integer fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {
        resultSet.next();
        return resultSet.getInt(fieldPrefix + "id");
    }
}
