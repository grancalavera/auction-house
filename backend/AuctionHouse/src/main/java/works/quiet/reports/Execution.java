package works.quiet.reports;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import works.quiet.auction.Bid;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class Execution {
    private int auctionId;
    private int bidId;
    private int bidderId;
    private ExecutionStatus status;

    private static Execution fromBidAndStatus(final Bid bid, final ExecutionStatus status) {
        return Execution.builder()
                .auctionId(bid.getAuctionId())
                .bidId(bid.getId())
                .bidderId(bid.getBidderId())
                .status(status)
                .build();
    }

    public static Execution fromBidFilled(final Bid bid) {
        return fromBidAndStatus(bid, ExecutionStatus.FILLED);
    }

    public static Execution fromBidNotFilled(final Bid bid) {
        return fromBidAndStatus(bid, ExecutionStatus.NOT_FILLED);
    }
}
