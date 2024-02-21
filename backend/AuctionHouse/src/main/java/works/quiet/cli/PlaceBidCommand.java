package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.auction.AuctionService;
import works.quiet.auction.Bid;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.logging.Level;

@CommandLine.Command(
        name = "bid",
        description = "Places a bid on another selle's auction.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class PlaceBidCommand extends CommandWithAdminAndAuction {
    @CommandLine.Option(names = {"-a", "--amount"}, description = "Bid amount.", required = true)
    private BigDecimal amount;

    @CommandLine.Option(names = {"-i", "--id"}, description = "Auction id.", required = true)
    private int auctionId;

    public PlaceBidCommand(
            final Level logLevel,
            final Resources resources, final AdminService adminService, final AuctionService auctionService) {
        super(logLevel, resources, adminService, auctionService);
    }

    @Override
    public void run() {
        adminService.assertIsNotBlocked();
        adminService.assertIsUser();
        var bid = Bid.builder()
                .bidderId(adminService.getCurrentUser().getId())
                .auctionId(auctionId)
                .amount(amount)
                .bidTimestamp(Instant.now())
                .build();

        spec.commandLine().getOut().println(bid);
    }
}
