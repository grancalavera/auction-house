package works.quiet.reports;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class Execution {
    private int auctionId;
    private int bidId;
    private int bidderId;
    private ExecutionStatus status;
}
