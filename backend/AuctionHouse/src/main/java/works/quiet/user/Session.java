package works.quiet.user;

import lombok.extern.java.Log;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@Log
public class Session {
    private static final Path SESSION_PATH = Path.of(System.getProperty("user.home"), ".ah-session");
    public void open(String username) throws Exception {
        try {
            Files.deleteIfExists(SESSION_PATH);
            Files.createFile(SESSION_PATH);
            Files.write(SESSION_PATH, username.getBytes(), StandardOpenOption.WRITE);
            log.info("Session open at: " + SESSION_PATH.toAbsolutePath());
        } catch(Exception ex) {
            log.severe(ex.toString());
            throw new Exception("Failed to open session.");
        }
    }

    public void close() throws Exception {
        try {
            Files.deleteIfExists(SESSION_PATH);
            log.info("Session closed at: " + SESSION_PATH.toAbsolutePath());
        } catch(Exception ex) {
            log.severe(ex.toString());
            throw new Exception("Failed to close session.");
        }
    }

    public Optional<String> getUsername () {
        try{
            String username = Files.readString(SESSION_PATH);
            log.info("Session username: '" + username + "'  .");
            return Optional.of(username);
        }catch (Exception ex){
            log.info("Session does not exist.");
            return Optional.empty();
        }
    }
}
