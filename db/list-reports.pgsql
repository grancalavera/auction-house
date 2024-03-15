select
  r.id,
  r.auctionId,
  a.closedAt as closingTime,
  r.revenue,
  r.soldQuantity,
  u.username as bidder,
  e.bidId,
  s.name as executionStatus
from reports r
  left join executions e on r.auctionId = e.auctionId
  left join users u on e.bidderId = u.id
  left join executionStatus s on e.status = s.id
  left join auctions a on r.auctionId = a.id
order by r.auctionId

