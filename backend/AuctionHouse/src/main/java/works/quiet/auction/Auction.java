package works.quiet.auction;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class Auction {
    private int id;
    private int sellerId;
    private String symbol;
    private int quantity;
    // price numeric(19, 4) not null,
    private BigDecimal price;
    @Builder.Default private AuctionStatus status = AuctionStatus.OPEN;
    @Builder.Default private List<Bid> bids = new ArrayList<>();
}
