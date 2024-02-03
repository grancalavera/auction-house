package works.quiet.cli;

import picocli.CommandLine.Command;

@Command(
        name = "auction-house",
        version = "auction-house 0.0.1",
        description = "An imaginary auction house that runs in the command line 😴💭",
        sortOptions = false,
        mixinStandardHelpOptions = true
)
public class MainProgram { }
