package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.resources.Resources;
import works.quiet.user.User;

import java.time.Instant;
import java.util.List;
import java.util.logging.Level;

@Log
public class AuctionService {

    private final Resources resources;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    public AuctionService(
            final Level logLevel,
            final Resources resources,
            final AuctionRepository auctionRepository,
            final BidRepository bidRepository
    ) {
        this.resources = resources;
        this.bidRepository = bidRepository;
        log.setLevel(logLevel);
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

    public List<Auction> listAuctionsForUser(final User user) {
        return auctionRepository.listAuctionsBySellerId(user.getId());
    }

    public List<Auction> listOpenAuctionsForBidder(final User bidder) {
        return auctionRepository.listOpenAuctionsForBidderId(bidder.getId());
    }

    public Auction closeAuctionForUserByAuctionId(final User user, final int auctionId) {
        var auction = auctionRepository
                .findAuctionBySellerIdAndAuctionId(user.getId(), auctionId)
                .orElseThrow(() -> new RuntimeException(
                        resources.getFormattedString("errors.cannotCloseAuction", auctionId, user.getId())
                ));

        if (auction.isClosed()) {
            throw new RuntimeException(resources.getFormattedString("errors.auctionAlreadyClosed", auctionId));
        }

        var closed = auction.toBuilder()
                .closedAt(Instant.now())
                .build();

        auctionRepository.save(closed);
        return closed;
    }
}
