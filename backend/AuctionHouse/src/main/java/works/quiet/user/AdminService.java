package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.reference.OrganisationModel;
import works.quiet.reference.OrganisationRepository;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class AdminService {
    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final Session session;
    private final UserValidator userValidator;

    public AdminService(
            final Level logLevel,
            final UserRepository userRepository,
            final OrganisationRepository organisationRepository,
            final Session session,
            final UserValidator userValidator
    ) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
        this.session = session;
        this.userValidator = userValidator;
        log.setLevel(logLevel);
    }

    public void login(final String username, final String password) throws Exception {
        Optional<UserModel> maybeUser = userRepository.findWithCredentials(username, password);

        if (maybeUser.isEmpty()) {
            log.info("Login attempt failed with username=" + username + ".");
            throw new Exception("Incorrect username or password.");
        }

        session.open(username);
    }

    public void logout() throws Exception {
        session.close();
        log.info("Logged out.");
    }

    public void assertIsNotBlocked() throws Exception {
        UserModel user = getCurrentUser();
        if (user.getAccountStatus() == AccountStatus.BLOCKED) {
            log.severe("Not authorised: username=\"" + user.getUsername() + "\" is blocked.");
            throw new Exception("Not authorised.");
        }
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

    public UserModel getCurrentUser() throws Exception {
        var maybeUser = session.getUsername().flatMap(userRepository::findByUsername);
        if (maybeUser.isPresent()) {
            return maybeUser.get();
        }
        throw new Exception("Not authenticated.");
    }

    private Role getCurrentUserRole() throws Exception {
        return getCurrentUser().getRole();
    }

    public int createUser(
            final String username,
            final String password,
            final String firstName,
            final String lastName,
            final String organisationName,
            final String roleName,
            final String accountStatusName
    ) throws Exception {

        OrganisationModel organisation = OrganisationModel.builder().name(organisationName).build();
        AccountStatus accountStatus = AccountStatus.valueOf(accountStatusName);
        Role role = Role.valueOf(roleName);

        UserModel user = UserModel.builder()
                .username(username)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .organisation(organisation)
                .role(role)
                .accountStatus(accountStatus)
                .build();

        userValidator.validate(user);

        int id = userRepository.createUser(user);
        log.info("created user with id=" + id);

        return id;
    }

    public void updateUser(final UserModel updatedUser) throws Exception {
        userValidator.validate(updatedUser);
        userRepository.updateUser(updatedUser);
        log.info("updated user with id=" + updatedUser.getId());
    }

    public List<UserModel> listUsers() {
        return userRepository.listUsers();
    }

    public List<OrganisationModel> listOrganisations() {
        return organisationRepository.listOrganisations();
    }

    public void blockUser(final int userId) throws Exception {
        UserModel user = unsafeFindUserById(userId);

        if (user.getId() == getCurrentUser().getId()) {
            String message = "Cannot block current user.";
            log.severe(message);
            throw new Exception(message);
        }

        if (user.getAccountStatus() == AccountStatus.BLOCKED) {
            log.info("User with user.id=" + userId + " is already blocked.");
            return;
        }

        UserModel blockedUser = user.toBuilder().accountStatus(AccountStatus.BLOCKED).build();
        userRepository.updateUser(blockedUser);
        log.info("Blocked user with user.id=" + userId + ".");
    }

    public void unblockUser(final int userId) throws Exception {
        UserModel user = unsafeFindUserById(userId);

        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
            log.info("User with user.id=" + userId + " is already active.");
            return;
        }

        UserModel unblockedUser = user.toBuilder().accountStatus(AccountStatus.ACTIVE).build();
        userRepository.updateUser(unblockedUser);
        log.info("Unlocked user with user.id=" + userId + ".");
    }

    public UserModel unsafeFindUserById(final int userId) throws Exception {
        Optional<UserModel> maybeUser = userRepository.findById(userId);
        if (maybeUser.isPresent()) {
            return maybeUser.get();
        }

        String message = "User with user.id=" + userId + " does not exist.";
        log.severe(message);
        throw new Exception(message);
    }
}
