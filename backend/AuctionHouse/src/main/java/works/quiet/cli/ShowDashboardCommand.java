package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.auction.AuctionService;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "show-dashboard",
        description = "Shows the status of all the user's activities in the Auction House.",
        sortOptions = false
)
public class ShowDashboardCommand extends CommandWithAdminAndAuction {

    public ShowDashboardCommand(
            final Level logLevel,
            final Resources resources, final AdminService adminService, final AuctionService auctionService) {
        super(logLevel, resources, adminService, auctionService);
    }

    @Override
    public void run() {
        adminService.assertIsNotBlocked();
        adminService.assertIsUser();
        var user = adminService.getCurrentUser();
        var dashboard = auctionService.getDashboardForUser(user);
        var out = spec.commandLine().getOut();

        out.println("My Auctions:");
        dashboard.getMyAuctions().forEach(out::println);
        out.println();
        out.println("Open Auctions:");
        dashboard.getOpenAuctions().forEach(out::println);
        out.println();
        out.println("My Reports:");
        dashboard.getMyReports().forEach(out::println);
        out.println();
        out.println("My Bids:");
        dashboard.getMyBids().forEach(out::println);
    }
}
