package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.reports.Execution;
import works.quiet.reports.Report;
import works.quiet.reports.ReportRepository;
import works.quiet.resources.Resources;
import works.quiet.user.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@Log
public class AuctionService {

    private final Resources resources;
    private final DBInterface dbInterface;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final ReportRepository reportRepository;
    private final Callable<Instant> now;

    public AuctionService(
            final Level logLevel,
            final Resources resources,
            final DBInterface dbInterface,
            final AuctionRepository auctionRepository,
            final BidRepository bidRepository,
            final ReportRepository reportRepository,
            final Callable<Instant> now
    ) {
        this.now = now;
        log.setLevel(logLevel);

        this.resources = resources;
        this.bidRepository = bidRepository;
        this.dbInterface = dbInterface;
        this.reportRepository = reportRepository;
        this.auctionRepository = auctionRepository;
    }

    public Auction createAuction(final Auction auction) {
        return auctionRepository.save(auction);
    }

    public Bid placeBid(final Bid bid) {
        var auction = auctionRepository.findById(bid.getAuctionId()).orElseThrow(() -> new RuntimeException(
                resources.getFormattedString("errors.auctionDoesNotExist", bid.getAuctionId())
        ));

        if (auction.getSellerId() == bid.getBidderId()) {
            throw new RuntimeException(resources.getFormattedString(
                    "errors.auctionBelongsToBidder",
                    bid.getAuctionId(), bid.getBidderId()));
        }

        // instead go to the DB and check if there's a report for this auction, if there is one it means is closed
        // if (auction.isClosed()) {
        //     throw new RuntimeException(resources.getFormattedString(
        //             "errors.auctionIsClosed",
        //             bid.getAuctionId()));
        // }

        return bidRepository.save(bid);
    }

    public Dashboard getDashboardForUser(final User user) {
        return Dashboard.builder()
                .myAuctions(this.listAuctionsForUser(user))
                .openAuctions(this.listOpenAuctionsForBidder(user))
                .build();
    }

    public List<Auction> listAuctionsForUser(final User user) {
        return auctionRepository.listAuctionsBySellerId(user.getId());
    }

    public List<Auction> listOpenAuctionsForBidder(final User bidder) {
        return auctionRepository.listOpenAuctionsForBidderId(bidder.getId());
    }

    public boolean isAuctionClosedByAuctionId(final int auctionId) {
        return reportRepository.existsByAuctionId(auctionId);
    }

    public void closeAuctionForUserByAuctionId(final User user, final int auctionId) {

        var auction = auctionRepository
                .findAuctionBySellerIdAndAuctionId(user.getId(), auctionId)
                .orElseThrow(() -> new RuntimeException(
                        resources.getFormattedString("errors.cannotCloseAuction", auctionId, user.getId())
                ));

        var alreadyClosed = reportRepository.existsByAuctionId(auctionId);

        if (alreadyClosed) {
            throw new RuntimeException(resources.getFormattedString("errors.auctionAlreadyClosed", auctionId));
        }

        var report = this.createReport(auction);

        dbInterface.beginTransaction();
        reportRepository.save(report);
        // report.getBids().forEach(bidRepository::save);
        dbInterface.commitTransaction();
    }

    public Report createReport(final Auction auction) {
        var atomicSoldQuantity = new AtomicInteger(0);
        var executions = new ArrayList<Execution>();

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
                        executions.add(Execution.ofBid(bid));
                        return;
                    }

                    var wanted = bid.getAmount().divide(auction.getPrice(), RoundingMode.FLOOR).intValue();
                    var filledQuantity = Math.min(availableQuantity, wanted);
                    atomicSoldQuantity.addAndGet(filledQuantity);
                    executions.add(Execution.ofBid(bid, filledQuantity));
                });

        var soldQuantity = atomicSoldQuantity.get();
        var revenue = auction.getPrice().multiply(BigDecimal.valueOf(soldQuantity));

        return Report.builder()
                .createdAt(unsafeNow())
                .auctionId(auction.getId())
                .revenue(revenue)
                .soldQuantity(soldQuantity)
                .executions(executions)
                .build();
    }

    private Instant unsafeNow() {
        try {
            return now.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
