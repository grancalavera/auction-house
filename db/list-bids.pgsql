select  
    b.id,
    b.auction_id,
    u.username as bidder,
    b.amount,
    b.bidtimestamp
from bids b
    left join users u on u.id = b.bidder_id;
