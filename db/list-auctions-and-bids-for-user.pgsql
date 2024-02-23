select
    a.id,
    a.seller_id as seller_id,
    u.username as seller,
    a.symbol,
    a.quantity,
    a.price,
    s.name as status,
    b.id as bid_id,
    b.amount as bid_amount,
    z.username as bidder,
    b.bidtimestamp as timestamp
from auctions a
    left join users u on a.seller_id = u.id
    left join auction_status s on a.status_id = s.id
    left join bids b on b.auction_id = a.id
    left join users z on b.bidder_id = z.id
where u.id=:user_id
