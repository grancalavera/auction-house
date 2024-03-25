package works.quiet.reports;

import works.quiet.db.Repository;

import java.util.List;

public interface ReportRepository extends Repository<Report> {

    List<Report> findAllBySellerId(int sellerId);

    boolean existsByAuctionId(int auctionId);
}
