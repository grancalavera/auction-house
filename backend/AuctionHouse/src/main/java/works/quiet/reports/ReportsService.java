package works.quiet.reports;

import lombok.extern.java.Log;
import works.quiet.auction.Auction;
import works.quiet.auction.Bid;
import works.quiet.db.Repository;
import works.quiet.resources.Resources;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;


@Log
public class ReportsService {
    private final Resources resources;
    private final Repository<Report> reportRepository;

    public ReportsService(
            final Level logLevel,
            final Resources resources,
            final Repository<Report> reportRepository
    ) {
        this.resources = resources;
        this.reportRepository = reportRepository;
        log.setLevel(logLevel);
    }

    public Report saveReport(final Report report) {
        var saved = reportRepository.save(report);
        log.info("saved report with id=" + saved.getId());
        var executions = new ArrayList<Execution>();

        report.getLoosingBids().forEach(bid -> {
            var execution = Execution.builder()
                    .auctionId(bid.getAuctionId())
                    .bidderId(bid.getBidderId())
                    .status(ExecutionStatus.NOT_FILLED)
                    .build();
            executions.add(execution);
        });

        report.getWinningBids().forEach(bid -> {
            var execution = Execution.builder()
                    .auctionId(bid.getAuctionId())
                    .bidderId(bid.getBidderId())
                    .status(ExecutionStatus.NOT_FILLED)
                    .build();
            executions.add(execution);
        });

        return saved;
    }


    public Report createReport(final Auction auction) {
        if (!auction.isClosed()) {
            throw new RuntimeException(resources.getString("errors.reportAuctionNotClosed"));
        }

        var winningBids = new ArrayList<Bid>();
        var loosingBids = new ArrayList<Bid>();
        var atomicSoldQuantity = new AtomicInteger(0);

        auction.getBids().stream()
                .sorted((a, b) -> {
                    var byAmountDesc = b.getAmount().compareTo(a.getAmount());
                    return byAmountDesc == 0
                            ? a.getCreatedAt().compareTo(b.getCreatedAt())
                            : byAmountDesc;
                })
                .forEach(bid -> {
                    var availableQuantity = auction.getQuantity() - atomicSoldQuantity.get();

                    if (availableQuantity == 0 || auction.getPrice().compareTo(bid.getAmount()) > 0) {
                        loosingBids.add(bid);
                        return;
                    }

                    var sold = bid.getAmount().divide(auction.getPrice(), RoundingMode.FLOOR).intValue();
                    atomicSoldQuantity.addAndGet(Math.min(availableQuantity, sold));
                    winningBids.add(bid);
                });

        var soldQuantity = atomicSoldQuantity.get();
        var revenue = auction.getPrice().multiply(BigDecimal.valueOf(soldQuantity));

        return Report.builder()
                .auctionId(auction.getId())
                .revenue(revenue)
                .soldQuantity(soldQuantity)
                .winningBids(winningBids)
                .loosingBids(loosingBids)
                .build();
    }
}
