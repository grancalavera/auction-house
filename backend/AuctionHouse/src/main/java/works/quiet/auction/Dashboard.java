package works.quiet.auction;

import lombok.Builder;
import lombok.Getter;
import works.quiet.reports.Report;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class Dashboard {
    @Builder.Default
    private List<Auction> myAuctions = new ArrayList<>();
    @Builder.Default
    private List<Auction> openAuctions = new ArrayList<>();
    @Builder.Default
    private List<Report> myReports = new ArrayList<>();
    @Builder.Default
    private List<Bid> myBids = new ArrayList<>();
}
