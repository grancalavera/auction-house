select
  r.id,
  r.auctionId,
  r.revenue,
  r.soldQuantity,
  r.createdAt,
  e.id as execution_id,
  e.bidId as execution_bidid,
  e.bidderId as execution_bidderid,
  e.filledQuantity as execution_filledquantity
from reports r
  left join executions e on r.auctionId = e.auctionId
order by r.createdAt

