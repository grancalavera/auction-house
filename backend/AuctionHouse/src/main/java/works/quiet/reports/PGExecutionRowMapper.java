package works.quiet.reports;

import works.quiet.db.PGMapper;

import java.sql.ResultSet;

public class PGExecutionRowMapper implements PGMapper<Execution> {
    @Override
    public Execution fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public Execution fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {
        return Execution.builder()
                .id(resultSet.getInt(fieldPrefix + "id"))
                .bidderId(resultSet.getInt(fieldPrefix + "bidderId"))
                .bidId(resultSet.getInt(fieldPrefix + "bidId"))
                .auctionId(resultSet.getInt(fieldPrefix + "auctionId"))
                .build();
    }
}
