package works.quiet.auction;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class Bid {
    private int id;
    private int bidderId;
    private int auctionId;
    private BigDecimal amount;
    private Instant createdAt;
    @Builder.Default
    private BidStatus status = BidStatus.PLACED;

    public Bid toFilled() {
        return this.toBuilder().status(BidStatus.FILLED).build();
    }

    public Bid toNotFilled() {
        return this.toBuilder().status(BidStatus.NOT_FILLED).build();
    }
}
