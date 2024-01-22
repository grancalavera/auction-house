package works.quiet.admin;

import lombok.extern.java.Log;
import works.quiet.user.UserDao;
import works.quiet.user.UserModel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@Log
public class AdminService {
    private final UserDao userDao;

    public AdminService(UserDao userDao) {
        this.userDao = userDao;
    }
    private static final String SESSION_FILE = ".ah-session";
    private static final String HOME = System.getProperty("user.home");

    public void login(String username, String password) throws Exception{
        Optional<UserModel> maybeUser=userDao.findWithCredentials(username, password);

        if (maybeUser.isEmpty()) {
            log.info("Login attempt failed with username="+username+".");
            throw new Exception("Wrong username or password.");
        }

        try {
            Path path =Path.of(HOME, SESSION_FILE);
            Files.deleteIfExists(path);
            Files.createFile(path);
            Files.write(path, username.getBytes(), StandardOpenOption.WRITE);
            log.info("session written to: " + path.toAbsolutePath());
        } catch(Exception ex) {
            log.severe(ex.toString());
            throw new Exception("Failed to initialize user session.");
        }
    }

    public void logout() throws Exception {
        try {
            Path path = Path.of(HOME, SESSION_FILE);
            Files.deleteIfExists(path);
            log.info("session destroyed at: " + path.toAbsolutePath());
        } catch(Exception ex) {
            log.severe(ex.toString());
            throw new Exception("Failed to destroy user session.");
        }
    }
}
