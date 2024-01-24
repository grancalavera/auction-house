package works.quiet.user;

import lombok.extern.java.Log;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Log
public class AdminService {
    private final UserDao userDao;
    private final Session session;

    public AdminService(UserDao userDao) {
        this.userDao = userDao;
        session = new Session();
    }

    public void login(String username, String password) throws Exception {
        Optional<UserModel> maybeUser=userDao.findWithCredentials(username, password);

        if (maybeUser.isEmpty()) {
            log.info("Login attempt failed with username="+username+".");
            throw new Exception("Wrong username or password.");
        }

        session.open(username);
    }

    public void logout() throws Exception {
        session.close();
    }

    public Optional<UserModel> currentUser () {
        return session.getUsername().flatMap(userDao::findByUsername);
    }
}
