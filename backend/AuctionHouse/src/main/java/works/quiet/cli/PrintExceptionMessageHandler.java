package works.quiet.cli;

import picocli.CommandLine;

// https://picocli.info/#_business_logic_exceptions
public class PrintExceptionMessageHandler implements CommandLine.IExecutionExceptionHandler {
    public int handleExecutionException(Exception ex, CommandLine cmd, CommandLine.ParseResult parseResult) {

        cmd.getErr().println(cmd.getColorScheme().errorText(ex.getMessage()));

        return cmd.getExitCodeExceptionMapper() == null
                ? cmd.getCommandSpec().exitCodeOnExecutionException()
                : cmd.getExitCodeExceptionMapper().getExitCode(ex);
    }
}