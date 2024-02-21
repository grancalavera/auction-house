package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.auction.AuctionService;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "list-open",
        description = "List all open auctions.",
        sortOptions = false
)
public class ListOpenAuctionsCommand extends CommandWithAdminAndAuction {

    public ListOpenAuctionsCommand(
            final Level logLevel,
            final Resources resources, final AdminService adminService, final AuctionService auctionService) {
        super(logLevel, resources, adminService, auctionService);
    }

    @Override
    public void run() {
        adminService.assertIsNotBlocked();
        adminService.assertIsUser();
        var user = adminService.getCurrentUser();
        auctionService.listOpenAuctionsForBidder(user).forEach(spec.commandLine().getOut()::println);
    }
}
