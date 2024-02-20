package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.auction.AuctionService;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

public abstract class CommandWithAdminAndAuction implements Runnable {
    protected final Level logLevel;
    protected final Resources resources;
    protected final AdminService adminService;
    protected final AuctionService auctionService;
    @CommandLine.Spec
    protected CommandLine.Model.CommandSpec spec;

    public CommandWithAdminAndAuction(
            final Level logLevel,
            final Resources resources, final AdminService adminService, final AuctionService auctionService) {
        this.logLevel = logLevel;
        this.resources = resources;
        this.adminService = adminService;
        this.auctionService = auctionService;
    }

}
