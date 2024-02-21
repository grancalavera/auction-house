package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.resources.Resources;
import works.quiet.user.User;

import java.util.List;
import java.util.logging.Level;

@Log
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final Resources resources;

    public AuctionService(
            final Level logLevel,
            final Resources resources,
            final AuctionRepository auctionRepository
    ) {
        this.resources = resources;
        log.setLevel(logLevel);
        this.auctionRepository = auctionRepository;
    }

    public Auction createAuction(final Auction auction) {
        return auctionRepository.save(auction);
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
                        resources.getFormattedString("errors.noSuchAuction", auctionId, user.getId())
                ));

        if (auction.getStatus() == AuctionStatus.CLOSED) {
            log.info("Auction.id=" + auctionId + " already closed.");
            return;
        }

        var closed = auction.toBuilder().status(AuctionStatus.CLOSED).build();
        auctionRepository.save(closed);
    }
}
