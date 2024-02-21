package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.auction.AuctionService;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "close",
        description = "Closes an existing auction under the current user's account.",
        sortOptions = false
)
public class CloseAuctionCommand extends CommandWithAdminAndAuction {

    @CommandLine.Parameters(paramLabel = "AUCTION_ID", description = "The auction id to close.")
    private int auctionId;

    public CloseAuctionCommand(
            final Level logLevel,
            final Resources resources, final AdminService adminService, final AuctionService auctionService) {
        super(logLevel, resources, adminService, auctionService);
    }

    @Override
    public void run() {
        adminService.assertIsUser();
        var user = adminService.getCurrentUser();
        auctionService.closeAuctionForUserByAuctionId(user, auctionId);
        spec.commandLine().getOut().println(resources.getFormattedString("messages.auctionClosed", auctionId));
    }
}
