package works.quiet.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrNormalized;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BoomCommandTest {

    @Test
    void call() throws Exception {
        CommandLine program = new CommandLine(new BoomCommand());

        var errText = tapSystemErrNormalized(() -> {
            var exitCode = program.execute();
            assertEquals(1, exitCode);
        });

        assertEquals("", errText);
    }
}
