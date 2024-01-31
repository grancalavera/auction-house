package works.quiet.cli;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "boom", hidden = true)
public class BoomCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        throw new Exception("💥");
    }
}
