package works.quiet.cli;

import picocli.CommandLine;

import java.util.logging.Level;

@CommandLine.Command(name = "show-config", hidden = true)
public class ShowConfigCommand implements Runnable {

    private final String dbUrl;
    private final String dbUser;
    private final Level logLevel;

    public ShowConfigCommand(final String dbUrl, final String dbUser, final Level logLevel) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.logLevel = logLevel;
    }

    @Override
    public void run() {
        System.out.printf("dbURL=%s\ndbUser=%s\nlogLevel=%s\n", dbUrl, dbUser, logLevel);
    }
}
