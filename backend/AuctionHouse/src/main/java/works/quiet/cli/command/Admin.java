package works.quiet.cli.command;

import picocli.CommandLine;

@CommandLine.Command(
        name="admin",
        description = "User and system administration.",
        subcommands = {
                CreateUser.class,
                CommandLine.HelpCommand.class
        }
)
public class Admin {
}
