select
    a.id,
    a.sellerId,
    a.symbol,
    a.quantity,
    a.price,
    a.createdAt,
    b.id as bid_id,
    b.amount as bid_amount,
    b.bidderId as bid_bidderId,
    b.createdAt as bid_createdAt
from auctions a
    left join bids b on b.auctionId = a.id
order by a.id, b.amount desc, b.createdAt asc;    
