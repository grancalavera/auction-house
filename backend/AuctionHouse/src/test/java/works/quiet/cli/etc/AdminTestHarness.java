package works.quiet.cli.etc;

import picocli.CommandLine;
import works.quiet.cli.CommandWithAdmin;
import works.quiet.cli.PrintExceptionMessageHandler;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.TimeZone;
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
        this.program = makeProgram(klass)
                .setErr(new PrintWriter(stdErr))
                .setOut(new PrintWriter(stdOut))
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler());
    }

    private CommandLine makeProgram(final Class<? extends CommandWithAdmin> klass) {
        try {
            CommandWithAdmin cmd = klass
                    .getDeclaredConstructor(Level.class, Resources.class, AdminService.class)
                    .newInstance(Level.OFF, resources, adminService);
            return new CommandLine(cmd);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String sanitizedOut() {
        return sanitizeStringWriter(stdOut);
    }

    public String sanitizedErr() {
        return sanitizeStringWriter(stdErr);
    }

    private String sanitizeStringWriter(final StringWriter writer) {
        return writer.toString().replace("\n", "");
    }
}
