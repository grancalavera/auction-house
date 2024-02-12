package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;
import java.util.logging.Level;

public abstract class CommandWithAdmin implements Callable<Integer> {
    protected final Level logLevel;
    protected final Resources resources;
    protected final AdminService adminService;
    @CommandLine.Spec
    protected CommandLine.Model.CommandSpec spec;
    public CommandWithAdmin(
            final Level logLevel, final Resources resources, final AdminService adminService) {
        this.logLevel = logLevel;
        this.resources = resources;
        this.adminService = adminService;
    }

}
