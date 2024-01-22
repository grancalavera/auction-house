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
    private static final Path SESSION_PATH = Path.of(System.getProperty("user.home"), ".ah-session");

    public void login(String username, String password) throws Exception{
        Optional<UserModel> maybeUser=userDao.findWithCredentials(username, password);

        if (maybeUser.isEmpty()) {
            log.info("Login attempt failed with username="+username+".");
            throw new Exception("Wrong username or password.");
        }

        try {
            Files.deleteIfExists(SESSION_PATH);
            Files.createFile(SESSION_PATH);
            Files.write(SESSION_PATH, username.getBytes(), StandardOpenOption.WRITE);
            log.info("session written to: " + SESSION_PATH.toAbsolutePath());
        } catch(Exception ex) {
            log.severe(ex.toString());
            throw new Exception("Failed to initialize user session.");
        }
    }

    public void logout() throws Exception {
        try {
            Files.deleteIfExists(SESSION_PATH);
            log.info("session destroyed at: " + SESSION_PATH.toAbsolutePath());
        } catch(Exception ex) {
            log.severe(ex.toString());
            throw new Exception("Failed to destroy user session.");
        }
    }

    public Optional<UserModel> currentUser (){
        try{
            String username = Files.readString(SESSION_PATH);
            log.info("current username: '" + username + "'");
            return Optional.empty();
        }catch (Exception ex){
            log.info("user session does not exist");
            return Optional.empty();
        }
    }
}
