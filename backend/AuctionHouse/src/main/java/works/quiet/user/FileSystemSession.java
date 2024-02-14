package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.resources.Resources;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class FileSystemSession implements Session {
    private static final Path SESSION_PATH = Path.of(System.getProperty("user.home"), ".ah-session");
    private final Resources resources;

    public FileSystemSession(final Level logLevel, final Resources resources) {
        this.resources = resources;
        log.setLevel(logLevel);
    }

    @Override
    public void open(final String username) {

        try {
            Files.deleteIfExists(SESSION_PATH);
            Files.createFile(SESSION_PATH);
            Files.write(SESSION_PATH, username.getBytes(), StandardOpenOption.WRITE);
            log.info("Session open at: " + SESSION_PATH.toAbsolutePath());
        } catch (final Exception ex) {
            log.severe("Failed to create session file.");
            log.severe(ex.toString());
            throw new RuntimeException(resources.getFormattedString("errors.openSessionFailed", username));
        }
    }


    @Override
    public void close() {
        try {
            Files.deleteIfExists(SESSION_PATH);
            log.info("Session closed at: " + SESSION_PATH.toAbsolutePath());
        } catch (final Exception ex) {
            log.severe(ex.toString());
            throw new RuntimeException(resources.getFormattedString("errors.closeSessionFailed"));
        }
    }

    @Override
    public Optional<String> getUsername() {
        try {
            String username = Files.readString(SESSION_PATH);
            log.info("Session username: '" + username + "'  .");
            return Optional.of(username);
        } catch (final Exception ex) {
            log.info("Session does not exist.");
            return Optional.empty();
        }
    }
}
