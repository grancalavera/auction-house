package works.quiet.auction;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import works.quiet.user.User;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class Auction {
    private int id;
    private User seller;
    private String symbol;
    private int quantity;
    // price numeric(19, 4) not null,
    private BigDecimal price;
    @Builder.Default  private AuctionStatus status = AuctionStatus.OPEN;
}
