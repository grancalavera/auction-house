package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.auction.AuctionService;
import works.quiet.reports.ReportsService;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "close",
        description = "Closes an existing auction under the current user's account.",
        sortOptions = false
)
public class CloseAuctionCommand extends CommandWithAdminAndAuction {

    private final ReportsService reportsService;

    @CommandLine.Parameters(paramLabel = "AUCTION_ID", description = "The auction id to close.")
    private int auctionId;

    public CloseAuctionCommand(
            final Level logLevel,
            final Resources resources, final AdminService adminService, final AuctionService auctionService,
            final ReportsService reportsService) {
        super(logLevel, resources, adminService, auctionService);
        this.reportsService = reportsService;
    }

    @Override
    public void run() {
        adminService.assertIsNotBlocked();
        adminService.assertIsUser();

        var user = adminService.getCurrentUser();
        var auction = auctionService.closeAuctionForUserByAuctionId(user, auctionId);
        var report = reportsService.createReport(auction);
        var savedReport = reportsService.saveReport(report);

        var out = spec.commandLine().getOut();
        out.println(resources.getFormattedString("messages.auctionClosed", auctionId));
        out.println(savedReport);
    }
}
