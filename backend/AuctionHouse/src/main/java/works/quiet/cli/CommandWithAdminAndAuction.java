package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "create-auction",
        description = "Creates a new auction.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public abstract class CommandWithAdminAndAuction implements Runnable {
    protected final Level logLevel;
    protected final Resources resources;
    protected final AdminService adminService;
    @CommandLine.Spec
    protected CommandLine.Model.CommandSpec spec;

    public CommandWithAdminAndAuction(
            final Level logLevel, final Resources resources, final AdminService adminService) {
        this.logLevel = logLevel;
        this.resources = resources;
        this.adminService = adminService;
    }

}
