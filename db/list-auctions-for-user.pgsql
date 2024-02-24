select
    auction.id,
    auction.seller_id,
    auction.symbol,
    auction.quantity,
    auction.price,
    auction.status_id,
    bid.id as bid_id,
    bid.auction_id as bid_auction_id,
    bid.bidder_id as bid_bidder_id,
    bid.amount as bid_amount,
    bid.createdAt as bid_createdAt
from auctions auction
    left join bids bid on bid.auction_id = auction.id
where auction.seller_id=:user_id
