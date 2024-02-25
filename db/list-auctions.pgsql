select  
    a.id, 
    a.sellerId as sellerId,
    u.username as seller,
    a.symbol,
    a.quantity,
    a.price,
    s.name as status,
    a.createdAt,
    a.closedAt
from auctions a
    left join users u on a.sellerId = u.id
    left join auctionStatus s on a.statusId = s.id;
