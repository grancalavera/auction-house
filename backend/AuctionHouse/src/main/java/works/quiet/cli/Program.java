package works.quiet.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import works.quiet.cli.command.Admin;
import works.quiet.cli.command.Login;
import works.quiet.cli.command.Logout;

@Command(
        name = "auction-house",
        version = "auction-house 0.0.1",
        description = "An imaginary auction house that runs in the command line 😴💭",
        mixinStandardHelpOptions = true,
        subcommands = {
                Login.class,
                Logout.class,
                Admin.class,
                CommandLine.HelpCommand.class
        }
)

public class Program { }
