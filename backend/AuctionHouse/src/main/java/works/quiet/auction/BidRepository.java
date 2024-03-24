package works.quiet.auction;

import works.quiet.db.Repository;

import java.util.List;

public interface BidRepository extends Repository<Bid> {
    List<Bid> findAllByBidderId(int bidderId);
}
