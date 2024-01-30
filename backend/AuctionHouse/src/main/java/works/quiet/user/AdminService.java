package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.reference.OrganisationModel;
import works.quiet.reference.OrganisationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class AdminService {
    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final Session session;
    private final UserValidator userValidator;

    public AdminService(Level logLevel, UserRepository userRepository, OrganisationRepository organisationRepository, Session session, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
        this.session = session;
        this.userValidator = userValidator;
        log.setLevel(logLevel);
    }

    public void login(String username, String password) throws Exception {
        Optional<UserModel> maybeUser = userRepository.findWithCredentials(username, password);

        if (maybeUser.isEmpty()) {
            log.info("Login attempt failed with username=" + username + ".");
            throw new BadLoginException();
        }

        session.open(username);
    }

    public void logout() throws Exception {
        session.close();
        log.info("Logged out.");
    }

    public void assertIsUser() throws Exception {
        if (getCurrentUserRole() != Role.USER) {
            throw new Exception("Not an user.");
        }
    }

    public void assertIsAdmin() throws Exception {
        if (getCurrentUserRole() != Role.ADMIN) {
            log.severe("Current user is not an admin, current role=" + getCurrentUserRole());
            throw new Exception("Not an admin.");
        }
    }

    public Optional<UserModel> getCurrentUser() {
        return session.getUsername().flatMap(userRepository::findByUsername);
    }

    public Role getCurrentUserRole() throws Exception {
        Optional<Role> role = getCurrentUser().map(UserModel::getRole);

        if (role.isEmpty()) {
            log.severe("Not authenticated.");
            throw new Exception("Not authenticated.");
        }

        return role.get();
    }

    public int createUser(String username, String password) throws Exception {

        userValidator.validateUsername(username);
        userValidator.validatePassword(password);

        final UserModel prototype = UserModel.builder().username(username).password(password).build();

        Optional<Integer> generatedId = userRepository.createUser(prototype);

        if (generatedId.isEmpty()) {
            throw new Exception("Failed to create new user");
        }

        log.info("created user: " + prototype.toBuilder().id(generatedId.get()).build());
        return generatedId.get();
    }

    public List<UserModel> listUsers() {
        return userRepository.listUsers();
    }

    public List<OrganisationModel> listOrganistions() {
        return organisationRepository.listOrganisations();
    }
}



