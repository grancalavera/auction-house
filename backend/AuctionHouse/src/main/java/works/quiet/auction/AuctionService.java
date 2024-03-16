package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.db.DBInterface;
import works.quiet.db.Repository;
import works.quiet.reports.Report;
import works.quiet.resources.Resources;
import works.quiet.user.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@Log
public class AuctionService {

    private final Resources resources;
    private final DBInterface dbInterface;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final Repository<Report> reportRepository;

    public AuctionService(
            final Level logLevel,
            final Resources resources,
            final DBInterface dbInterface,
            final AuctionRepository auctionRepository,
            final BidRepository bidRepository,
            final Repository<Report> reportRepository
    ) {
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

        if (auction.isClosed()) {
            throw new RuntimeException(resources.getFormattedString(
                    "errors.auctionIsClosed",
                    bid.getAuctionId()));
        }

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

    public void closeAuctionForUserByAuctionId(final User user, final int auctionId) {
        var auction = auctionRepository
                .findAuctionBySellerIdAndAuctionId(user.getId(), auctionId)
                .orElseThrow(() -> new RuntimeException(
                        resources.getFormattedString("errors.cannotCloseAuction", auctionId, user.getId())
                ));

        if (auction.isClosed()) {
            throw new RuntimeException(resources.getFormattedString("errors.auctionAlreadyClosed", auctionId));
        }

        var closed = auction.toBuilder().closedAt(Instant.now()).build();
        var report = this.createReport(closed);

        dbInterface.beginTransaction();
        auctionRepository.save(closed);
        reportRepository.save(report);
        report.getBids().forEach(bidRepository::save);
        dbInterface.commitTransaction();
    }

    public Report createReport(final Auction auction) {
        if (!auction.isClosed()) {
            throw new RuntimeException(resources.getString("errors.reportAuctionNotClosed"));
        }

        var atomicSoldQuantity = new AtomicInteger(0);
        var bids = new ArrayList<Bid>();

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
                        bids.add(bid.toNotFilled());
                        return;
                    }

                    var sold = bid.getAmount().divide(auction.getPrice(), RoundingMode.FLOOR).intValue();
                    atomicSoldQuantity.addAndGet(Math.min(availableQuantity, sold));
                    bids.add(bid.toFilled());
                });

        var soldQuantity = atomicSoldQuantity.get();
        var revenue = auction.getPrice().multiply(BigDecimal.valueOf(soldQuantity));

        return Report.builder()
                .auctionId(auction.getId())
                .revenue(revenue)
                .soldQuantity(soldQuantity)
                .bids(bids)
                .build();
    }
}
