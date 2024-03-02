package works.quiet.reports;

import lombok.extern.java.Log;
import works.quiet.auction.Auction;
import works.quiet.auction.Bid;
import works.quiet.resources.Resources;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;


@Log
public class ReportsService {
    private final Resources resources;

    public ReportsService(final Level logLevel, final Resources resources) {
        this.resources = resources;
        log.setLevel(logLevel);
    }

    public Report createReport(final Auction auction) {
        if (!auction.isClosed()) {
            throw new RuntimeException(resources.getString("errors.reportAuctionNotClosed"));
        }

        var winningBids = new ArrayList<Bid>();
        var atomicSoldQuantity = new AtomicInteger(0);

        auction.getBids().stream()
                .sorted((a, b) -> {
                    var byAmountDesc = b.getAmount().compareTo(a.getAmount());

                    if (byAmountDesc != 0) {
                        return byAmountDesc;
                    }

                    return a.getCreatedAt().compareTo(b.getCreatedAt());

                })
                .forEach(bid -> {
                    var availableQuantity = auction.getQuantity() - atomicSoldQuantity.get();

                    if (availableQuantity == 0) {
                        return;
                    }

                    if (auction.getPrice().compareTo(bid.getAmount()) > 0) {
                        return;
                    }

                    var sold = bid.getAmount().divide(auction.getPrice(), RoundingMode.FLOOR).intValue();
                    atomicSoldQuantity.addAndGet(Math.min(availableQuantity, sold));
                    winningBids.add(bid);
                });

        var soldQuantity = atomicSoldQuantity.get();
        var revenue = auction.getPrice().multiply(BigDecimal.valueOf(soldQuantity));

        return Report.builder()
                .auctionClosedAt(auction.getClosedAt())
                .auctionId(auction.getId())
                .revenue(revenue)
                .soldQuantity(soldQuantity)
                .bids(winningBids)
                .build();
    }
}
