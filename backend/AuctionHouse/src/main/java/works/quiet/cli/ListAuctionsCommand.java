package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.auction.AuctionService;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "list",
        description = "List all auctions for the current user.",
        sortOptions = false
)
public class ListAuctionsCommand extends CommandWithAdminAndAuction {

    public ListAuctionsCommand(
            final Level logLevel,
            final Resources resources, final AdminService adminService, final AuctionService auctionService) {
        super(logLevel, resources, adminService, auctionService);
    }

    @Override
    public void run() {
        adminService.assertIsUser();
        var user = adminService.getCurrentUser();
        auctionService.listAuctions(user).forEach(spec.commandLine().getOut()::println);
    }
}
