package works.quiet.cli;

import picocli.CommandLine;

@CommandLine.Command(name = "boom", hidden = true)
public class BoomCommand implements Runnable {

    @Override
    public void run() {
        throw new RuntimeException("💥");
    }
}
