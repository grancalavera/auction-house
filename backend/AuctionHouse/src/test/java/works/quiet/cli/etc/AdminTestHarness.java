package works.quiet.cli.etc;

import picocli.CommandLine;
import works.quiet.cli.CommandWithAdmin;
import works.quiet.cli.PrintExceptionMessageHandler;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;

import static org.mockito.Mockito.mock;

public class AdminTestHarness {
    public final Resources resources;
    public final AdminService adminService;
    public final CommandLine program;
    private final StringWriter stdErr;
    private final StringWriter stdOut;

    public AdminTestHarness(final Class<? extends CommandWithAdmin> klass) {
        this.resources = new Resources();
        this.adminService = mock();
        this.stdErr = new StringWriter();
        this.stdOut = new StringWriter();
        var command = makeCommand(klass);
        this.program = new CommandLine(command)
                .setErr(new PrintWriter(stdErr))
                .setOut(new PrintWriter(stdOut))
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler());
    }

    public String sanitizedOut() {
        return sanitizeStringWriter(stdOut);
    }

    public String sanitizedErr() {
        return sanitizeStringWriter(stdErr);
    }

    private CommandWithAdmin makeCommand(final Class<? extends CommandWithAdmin> klass) {
        try {
            return klass
                    .getDeclaredConstructor(Level.class, Resources.class, AdminService.class)
                    .newInstance(Level.OFF, resources, adminService);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String sanitizeStringWriter(final StringWriter writer) {
        return writer.toString().replace("\n", "");
    }
}
