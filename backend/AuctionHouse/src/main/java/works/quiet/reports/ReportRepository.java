package works.quiet.reports;

import works.quiet.db.Repository;
import works.quiet.user.User;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends Repository<Report> {

    List<Report> findReportsForUser(User user);
    Optional<Report> findReportByAuctionId(int auctionId);
    boolean existsByAuctionId(int auctionId);
}
