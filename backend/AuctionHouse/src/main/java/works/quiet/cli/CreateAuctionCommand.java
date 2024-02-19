package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.auction.Auction;
import works.quiet.auction.AuctionStatus;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.math.BigDecimal;
import java.util.logging.Level;

@CommandLine.Command(name = "create", description = "Creates a new auction under the current user's account.", sortOptions = false)
public class CreateAuctionCommand extends CommandWithAdminAndAuction {

    @CommandLine.Option(
            names = {"-s", "--symbol"},
            required = true,
            description = "The symbol to open for auction."
    )
    private String symbol;

    @CommandLine.Option(
            names = {"-q", "--quantity"},
            description = "The quantity of contracts of <symbol> open for auction.",
            required = true
    )
    private int quantity;

    @CommandLine.Option(
            names = {"-p", "--price"},
            description = "The auction's starting price.",
            required = true
    )
    private BigDecimal price;

    public CreateAuctionCommand(final Level logLevel, final Resources resources, final AdminService adminService) {
        super(logLevel, resources, adminService);
    }

    @Override
    public void run() {
        adminService.assertIsUser();
        var auction = Auction.builder()
                .symbol(symbol)
                .quantity(quantity)
                .price(price)
                .seller(adminService.getCurrentUser())
                .build();

        spec.commandLine().getOut().println(auction);
    }
}
