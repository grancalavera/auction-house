package works.quiet.reports;

import works.quiet.db.PGMapper;

import java.sql.ResultSet;

public class PGReportRowMapper implements PGMapper<Report> {
    @Override
    public Report fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public Report fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {
        return null;
    }
}
