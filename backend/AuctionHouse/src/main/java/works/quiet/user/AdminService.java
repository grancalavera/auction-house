package works.quiet.user;

import lombok.extern.java.Log;

import java.util.Optional;

@Log
public class AdminService {
    private final UserRepository userRepository;
    private final Session session;
    private final UserValidator userValidator;

    public AdminService(UserRepository userRepository, Session session, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.session = session;
        this.userValidator = userValidator;
    }

    public void login(String username, String password) throws Exception {
        Optional<UserModel> maybeUser = userRepository.findWithCredentials(username, password);

        if (maybeUser.isEmpty()) {
            log.info("Login attempt failed with username=" + username + ".");
            throw new Exception("Wrong username or password.");
        }

        session.open(username);
    }

    public void logout() throws Exception {
        session.close();
    }


    public void assertIsUser() throws Exception {
        if (getCurrentUserRole() != Role.USER) {
            throw new Exception("Not an user.");
        }
    }

    public void assertIsAdmin() throws Exception {
        if (getCurrentUserRole() != Role.ADMIN) {
            System.out.println("current user role: " + getCurrentUserRole());
            throw new Exception("Not an admin.");
        }
    }

    public Optional<UserModel> getCurrentUser() {
        return session.getUsername().flatMap(userRepository::findByUsername);
    }

    public Role getCurrentUserRole() throws Exception {
        Optional<Role> role = getCurrentUser().map(UserModel::getRole);

        if (role.isEmpty()) {
            throw new Exception("Not authenticated");
        }

        return role.get();
    }

    public UserModel createUser(String username, String password, int roleId) throws Exception {
        userValidator.validateUsername(username);
        userValidator.validatePassword(password);
        final Role role = Role.ofInt(roleId);
        return UserModel
                .builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

    }
}
