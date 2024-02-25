select
    a.id,
    a.sellerId as sellerId,
    u.username as seller,
    a.symbol,
    a.quantity,
    a.price,
    s.name as status,
    b.id as bid_id,
    b.amount as bid_amount,
    z.username as bidder,
    b.createdAt as bid_createdAt
from auctions a
    left join users u on a.sellerId = u.id
    left join auctionStatus s on a.statusId = s.id
    left join bids b on b.auctionId = a.id
    left join users z on b.bidderId = z.id;
