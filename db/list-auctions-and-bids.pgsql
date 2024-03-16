select
    a.id,
    a.sellerId,
    a.symbol,
    a.quantity,
    a.price,
    a.createdAt,
    a.closedAt,
    b.id as bid_id,
    b.amount as bid_amount,
    b.bidderId as bid_bidderId,
    b.createdAt as bid_createdAt,
    s.name as bid_status
from auctions a
    left join bids b on b.auctionId = a.id
    left join bidStatus s on b.status = s.id
order by a.id, b.id;    
