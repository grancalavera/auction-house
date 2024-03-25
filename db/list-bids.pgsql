select  
    b.id,
    b.auctionId,
    u.username as bidder,
    b.amount,
    b.createdAt,
    b.status
from bids b
    left join users u on u.id = b.bidderId;
