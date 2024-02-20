package works.quiet.auction;

import lombok.extern.java.Log;
import works.quiet.user.User;

import java.util.List;
import java.util.logging.Level;

@Log
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public AuctionService(
            final Level logLevel,
            final AuctionRepository auctionRepository) {
        log.setLevel(logLevel);
        this.auctionRepository = auctionRepository;
    }

    public Auction createAuction(final Auction auction) {
        return auctionRepository.save(auction);
    }

    public List<Auction> listAuctions(final User user) {
        return auctionRepository.listAuctionsBySellerId(user.getId());
    }
}
