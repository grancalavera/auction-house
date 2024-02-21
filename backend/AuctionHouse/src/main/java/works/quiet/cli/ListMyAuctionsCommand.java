package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.auction.AuctionService;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "list-mine",
        description = "List all open and closed auctions owned by the current user.",
        sortOptions = false
)
public class ListMyAuctionsCommand extends CommandWithAdminAndAuction {

    public ListMyAuctionsCommand(
            final Level logLevel,
            final Resources resources, final AdminService adminService, final AuctionService auctionService) {
        super(logLevel, resources, adminService, auctionService);
    }

    @Override
    public void run() {
        adminService.assertIsNotBlocked();
        adminService.assertIsUser();
        var user = adminService.getCurrentUser();
        auctionService.listAuctionsForUser(user).forEach(spec.commandLine().getOut()::println);
    }
}
