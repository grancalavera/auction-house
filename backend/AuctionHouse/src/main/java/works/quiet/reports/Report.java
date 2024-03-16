package works.quiet.reports;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import works.quiet.auction.Bid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class Report {
    private int id;
    private int auctionId;

    @Builder.Default
    private BigDecimal revenue = BigDecimal.valueOf(0);
    @Builder.Default
    private int soldQuantity = 0;
    @Builder.Default
    private List<Bid> bids = new ArrayList<>();
}
