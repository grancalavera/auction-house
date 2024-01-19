package works.quiet;


import picocli.CommandLine;
import works.quiet.cli.AuctionHouse;

class Main {
    public static void main(String ... argv) {
        int exitCode = new CommandLine(new AuctionHouse()).execute(argv);
        System.exit(exitCode);
    }
}