package works.quiet.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoomCommandTest {

    @Test
    void call() {
        StringWriter stdErr = new StringWriter();
        CommandLine program = new CommandLine(new BoomCommand());
        program.setErr(new PrintWriter(stdErr));

        var exitCode = program
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute();

        assertEquals(1, exitCode);
        assertEquals("💥\n", stdErr.toString());
    }
}
