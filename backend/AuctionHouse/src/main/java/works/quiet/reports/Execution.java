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
    private int id;
    private int auctionId;
    private int bidId;
    private int bidderId;
    @Builder.Default
    private int filledQuantity = 0;

    public static Execution ofBid(final Bid bid) {
        return ofBid(bid, 0);
    }
    public static Execution ofBid(final Bid bid, final int filledQuantity) {
        return Execution.builder()
                .bidId(bid.getId())
                .auctionId(bid.getAuctionId())
                .bidderId(bid.getBidderId())
                .filledQuantity(filledQuantity)
                .build();
    }
}
