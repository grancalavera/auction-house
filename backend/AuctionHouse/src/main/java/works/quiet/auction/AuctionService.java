package works.quiet.auction;

import lombok.extern.java.Log;

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
}
