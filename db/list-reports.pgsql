select
  r.id,
  r.auctionId,
  a.closedAt as closingTime,
  r.revenue,
  r.soldQuantity,
  u.username as bidder,
  e.id as bidId,
  s.name as bidStatus
from reports r
  left join bids e on r.auctionId = e.auctionId
  left join users u on e.bidderId = u.id
  left join bidStatus s on e.status = s.id
  left join auctions a on r.auctionId = a.id
order by r.auctionId

