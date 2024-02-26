package works.quiet.db;

import java.sql.ResultSet;

public class PGUpsertMapper implements PGMapper<Integer> {
    @Override
    public Integer fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public Integer fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {
        return null;
    }
}
