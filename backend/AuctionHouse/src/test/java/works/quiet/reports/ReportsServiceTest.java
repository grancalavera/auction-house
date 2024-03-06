package works.quiet.reports;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import works.quiet.auction.Auction;
import works.quiet.auction.Bid;
import works.quiet.resources.Resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class ReportsServiceTest {
    private ReportsService service;
    private Resources resources;

    private Auction.AuctionBuilder auctionBuilder;
    private Report.ReportBuilder reportBuilder;

    @BeforeEach
    void setup() {
        int auctionId = 1;
        Instant closedAt = Instant.ofEpochSecond(10);
        auctionBuilder = Auction.builder().id(auctionId).closedAt(closedAt);
        reportBuilder = Report.builder().auctionId(auctionId);
        resources = new Resources();
        service = new ReportsService(Level.OFF, resources, mock());
    }

    @Test
    @DisplayName("Should throw when Auction is not closed")
    void openAuction() {
        var auction = Auction.builder().build();
        var exception = assertThrows(RuntimeException.class, () -> service.createReport(auction));
        assertEquals(resources.getString("errors.reportAuctionNotClosed"), exception.getMessage());
    }

    @Test
    @DisplayName("Should create an empty report for an auction with no bids")
    void auctionWithNoBids() {
        var price = BigDecimal.valueOf(1);
        var auction = auctionBuilder.price(price).build();
        var expectedReport = reportBuilder.build();
        var actualReport = service.createReport(auction);
        assertEquals(expectedReport, actualReport);
    }

    @Test
    @DisplayName("Should create an empty report for an auction with bids below asking price")
    void bidsBelowAskingPrice() {
        var auction = auctionBuilder.price(BigDecimal.valueOf(2)).build();
        var bid = Bid.builder().amount(BigDecimal.valueOf(1)).build();
        auction.getBids().add(bid);
        var expectedReport = reportBuilder.build();
        expectedReport.getLoosingBids().add(bid);
        var actualReport = service.createReport(auction);
        assertEquals(expectedReport, actualReport);
    }

    @Test
    @DisplayName("Should create a non-empty report for an auction with bids at asking price")
    void bidAtAskingPrice() {
        var price = BigDecimal.valueOf(1);
        var quantity = 1;

        var auction = auctionBuilder.price(price)
                .quantity(quantity)
                .build();

        var bidBuilder = Bid.builder().amount(price);
        auction.getBids().add(bidBuilder.build());

        var expectedReport = reportBuilder
                .revenue(price)
                .soldQuantity(quantity)
                .build();

        expectedReport.getWinningBids().add(bidBuilder.build());

        var actualReport = service.createReport(auction);

        assertEquals(expectedReport, actualReport);
    }

    @Test
    @DisplayName("Bids should be filled when possible")
    void fillBids() {
        var price = BigDecimal.valueOf(1);
        var quantity = 2;
        var totalFill = price.multiply(BigDecimal.valueOf(quantity));

        var auction = auctionBuilder.price(price)
                .quantity(quantity)
                .build();

        var bid = Bid.builder().amount(totalFill).build();
        auction.getBids().add(bid);

        var expectedReport = reportBuilder
                .revenue(totalFill)
                .soldQuantity(quantity)
                .build();

        expectedReport.getWinningBids().add(bid);

        var actualReport = service.createReport(auction);

        assertEquals(expectedReport, actualReport);
    }

    @Test
    @DisplayName("Should not fill bids if a quantity is exceeded")
    void doNotOverFill() {
        var price = BigDecimal.valueOf(1);
        var quantity = 1;

        var auction = auctionBuilder.price(price)
                .quantity(quantity)
                .build();
        var bid1 = Bid.builder()
                .id(1)
                .createdAt(Instant.now())
                .amount(price)
                .build();

        var bid2 = Bid.builder()
                .id(2)
                .createdAt(Instant.now())
                .amount(price)
                .build();

        auction.getBids().add(bid1);
        auction.getBids().add(bid2);

        var expectedReport = reportBuilder
                .revenue(price)
                .soldQuantity(quantity)
                .build();
        expectedReport.getWinningBids().add(bid1);
        expectedReport.getLoosingBids().add(bid2);

        var actualReport = service.createReport(auction);

        assertEquals(expectedReport, actualReport);
    }

    @Test
    @DisplayName("Should not sell more than is available")
    void doNotOverSell() {
        var price = BigDecimal.valueOf(1);
        var quantity = 1;
        var doublePrice = BigDecimal.valueOf(2);

        var auction = auctionBuilder.price(price)
                .quantity(quantity)
                .build();
        var bid = Bid.builder().amount(doublePrice).build();
        auction.getBids().add(bid);

        var expectedReport = reportBuilder
                .revenue(price)
                .soldQuantity(quantity)
                .build();
        expectedReport.getWinningBids().add(bid);

        var actualReport = service.createReport(auction);

        assertEquals(expectedReport, actualReport);
    }

    @Test
    @DisplayName("Higher bids should be processed first, and sort for auctions bids should be preserved")
    void higherBidGoesFirst() {
        var quantity = 3;
        var price = BigDecimal.valueOf(1);
        var doublePrice = BigDecimal.valueOf(2);

        var auction = auctionBuilder.price(price).quantity(quantity).build();
        var lowerBid = Bid.builder().amount(price).build();
        var higherBid = Bid.builder().amount(doublePrice).build();
        auction.getBids().add(lowerBid);
        auction.getBids().add(higherBid);

        var expectedReport = reportBuilder
                .revenue(price.multiply(BigDecimal.valueOf(quantity)))
                .soldQuantity(quantity)
                .build();

        expectedReport.getWinningBids().add(higherBid);
        expectedReport.getWinningBids().add(lowerBid);

        var actualReport = service.createReport(auction);

        assertEquals(expectedReport, actualReport);
    }

    @Test
    @DisplayName("Ties should be filled by date")
    void fillTiesByDate() {
        var quantity = 3;
        var price = BigDecimal.valueOf(1);

        var auction = auctionBuilder.price(price).quantity(quantity).build();

        var bid1 = Bid.builder().id(1).amount(price).createdAt(Instant.ofEpochSecond(3)).build();
        var bid2 = Bid.builder().id(2).amount(price).createdAt(Instant.ofEpochSecond(2)).build();
        var bid3 = Bid.builder().id(3).amount(price).createdAt(Instant.ofEpochSecond(1)).build();

        auction.getBids().add(bid1);
        auction.getBids().add(bid2);
        auction.getBids().add(bid3);

        var expectedReport = reportBuilder
                .revenue(price.multiply(BigDecimal.valueOf(quantity)))
                .soldQuantity(quantity)
                .build();

        expectedReport.getWinningBids().add(bid3);
        expectedReport.getWinningBids().add(bid2);
        expectedReport.getWinningBids().add(bid1);

        var actualReport = service.createReport(auction);

        assertEquals(expectedReport, actualReport);
    }

    @Test
    @DisplayName("Bids should be filled by price")
    void byPrice() {
        var quantity = 6;
        var price = BigDecimal.valueOf(1);       // 1
        var doublePrice = BigDecimal.valueOf(2); // 2
        var triplePrice = BigDecimal.valueOf(3); // 3

        var auction = auctionBuilder.price(price).quantity(quantity).build();

        var bid1 = Bid.builder()
                .id(1)
                .amount(price)
                .createdAt(Instant.ofEpochSecond(0))
                .build();

        var bid2 = Bid.builder()
                .id(2)
                .amount(doublePrice)
                .createdAt(Instant.ofEpochSecond(0))
                .build();

        var bid3 = Bid.builder()
                .id(3)
                .amount(triplePrice)
                .createdAt(Instant.ofEpochSecond(0))
                .build();

        auction.getBids().add(bid1);
        auction.getBids().add(bid2);
        auction.getBids().add(bid3);

        var expectedReport = reportBuilder
                .revenue(price.multiply(BigDecimal.valueOf(quantity)))
                .soldQuantity(quantity)
                .build();

        expectedReport.getWinningBids().add(bid3);
        expectedReport.getWinningBids().add(bid2);
        expectedReport.getWinningBids().add(bid1);

        var actualReport = service.createReport(auction);

        assertEquals(expectedReport, actualReport);
    }
}
