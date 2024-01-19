package works.quiet.cli;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

@Command(name = "auction-house", version = "auction-house 0.0.1", mixinStandardHelpOptions = true)
public class AuctionHouse implements Runnable {

    @Option(names = { "-s", "--size" }, description = "Size")
    int fontSize = 19;
    @Override
    public void run() {
        System.out.printf("OK Computer %d", fontSize);
    }
}
