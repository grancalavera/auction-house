package works.quiet.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoomCommandTest {

    @Test
    void call() {
        CommandLine program = new CommandLine(new BoomCommand());
        int exitCode = program.execute();
        assertEquals(1, exitCode);
    }
}
