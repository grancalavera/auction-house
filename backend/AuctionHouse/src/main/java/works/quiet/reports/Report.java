package works.quiet.reports;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class Report {
    private int id;
    private int auctionId;
    private int sellerId;
    private Instant createdAt;

    @Builder.Default
    private BigDecimal revenue = BigDecimal.valueOf(0);
    @Builder.Default
    private int soldQuantity = 0;
    @Builder.Default
    private List<Execution> executions = new ArrayList<>();
}
