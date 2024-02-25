select
    auction.id,
    auction.sellerId,
    auction.symbol,
    auction.quantity,
    auction.price,
    auction.statusId,
    bid.id as bid_id,
    bid.auctionId as bid_auctionId,
    bid.bidderId as bid_bidderId,
    bid.amount as bid_amount,
    bid.createdAt as bid_createdAt
from auctions auction
    left join bids bid on bid.auctionId = auction.id
where auction.sellerId=:user_id
