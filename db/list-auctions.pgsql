select  
    a.id, 
    u.username as seller,
    a.symbol,
    a.quantity,
    a.price,
    s.name as status
from auctions a
    left join users u on a.seller_id = u.id
    left join auction_status s on a.status_id = s.id;
