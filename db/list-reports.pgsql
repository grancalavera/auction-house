select
  r.id,
  r.auctionId,
  r.revenue,
  r.soldQuantity,
  e.id as execution_id,
  e.bidId as execution_bidid,
  e.filledQuantity as execution_filledquantity
from reports r
  left join executions e on r.auctionId = e.auctionId
order by r.auctionId

