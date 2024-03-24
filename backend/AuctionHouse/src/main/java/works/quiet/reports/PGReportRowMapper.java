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
        return Report.builder()
                .id(resultSet.getInt(fieldPrefix + "id"))
                .sellerId(resultSet.getInt(fieldPrefix + "sellerId"))
                .createdAt(resultSet.getTimestamp(fieldPrefix + "createdAt").toInstant())
                .revenue(resultSet.getBigDecimal(fieldPrefix + "revenue"))
                .auctionId(resultSet.getInt(fieldPrefix + "auctionId"))
                .build();
    }
}
