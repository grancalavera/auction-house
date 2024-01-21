package works.quiet;

import picocli.CommandLine;
import works.quiet.cli.Program;

class AuctionHouse {
    public static void main(String ... argv) {
        int exitCode = new CommandLine(new Program()).execute(argv);
        System.exit(exitCode);
    }
}