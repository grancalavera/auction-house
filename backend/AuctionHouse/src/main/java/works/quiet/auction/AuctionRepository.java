package works.quiet.auction;

import works.quiet.db.Repository;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends Repository<Auction> {
    List<Auction> listAuctionsBySellerId(int sellerId);

    List<Auction> listOpenAuctionsForBidderId(int bidderId);

    Optional<Auction> findAuctionBySellerIdAndAuctionId(int sellerId, int auctionId);
}
