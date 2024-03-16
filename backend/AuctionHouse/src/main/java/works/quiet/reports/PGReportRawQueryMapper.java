package works.quiet.reports;

import lombok.extern.java.Log;
import works.quiet.auction.Bid;
import works.quiet.db.PGMapper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Log
public class PGReportRawQueryMapper implements PGMapper<List<Report>> {

    private final PGMapper<Report> reportRowMapper;
    private final PGMapper<Bid> bidRowMapper;


    public PGReportRawQueryMapper(
            final Level logLevel,
            final PGMapper<Report> reportRowMapper,
            final PGMapper<Bid> bidRowMapper
    ) {
        this.reportRowMapper = reportRowMapper;
        this.bidRowMapper = bidRowMapper;
        log.setLevel(logLevel);
    }

    @Override
    public List<Report> fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public List<Report> fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {
        // A map and a list yes... a list to keep the order of the query, and a map to
        // find reports quickly and add more bids to them as the ResultSet is mapped.
        Map<Integer, Report> index = new HashMap<>();
        ArrayList<Report> result = new ArrayList<>();

        while (resultSet.next()) {
            var auctionId = resultSet.getInt("id");
            Report row;

            if (index.containsKey(auctionId)) {
                row = index.get(auctionId);
            } else {
                row = reportRowMapper.fromResulSet(resultSet);
                result.add(row);
                index.put(auctionId, row);
            }

            // all bid-related columns use the "bid_" prefix
            if (resultSet.getInt("bid_id") == 0) {
                break;
            }

            var bid = bidRowMapper.fromResulSet("bid_", resultSet);
            row.getBids().add(bid);
        }

        return result;
    }
}
