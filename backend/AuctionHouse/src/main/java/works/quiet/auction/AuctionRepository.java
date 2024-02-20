package works.quiet.auction;

import works.quiet.db.Repository;

import java.util.List;

public interface AuctionRepository extends Repository<Auction> {
    List<Auction> listAuctionsBySellerId(int sellerId);
}
