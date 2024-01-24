package works.quiet.user;

import lombok.extern.java.Log;

import java.util.Optional;

@Log
public class AdminService {
    private final UserRepository userRepository;
    private final Session session;

    public AdminService(UserRepository userRepository, Session session) {
        this.userRepository = userRepository;
        this.session = session;
    }

    public void login(String username, String password) throws Exception {
        Optional<UserModel> maybeUser= userRepository.findWithCredentials(username, password);

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
        return session.getUsername().flatMap(userRepository::findByUsername);
    }
}
