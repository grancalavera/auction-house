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
            String username,
            String password,
            String firstName,
            String lastName,
            String organisationName,
            String roleName,
            String accountStatusName
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

    public void updateUser(
            int userId,
            String username,
            String password,
            String firstName,
            String lastName,
            String organisationName,
            String roleName,
            String accountStatusName
    ) throws Exception {

        UserModel.UserModelBuilder updateBuilder = unsafeFindUserById(userId).toBuilder();

        if (username != null) {
            updateBuilder.username(username);
        }

        if (password != null) {
            updateBuilder.password(password);
        }

        if (firstName != null) {
            updateBuilder.firstName(firstName);
        }

        if (lastName != null) {
            updateBuilder.lastName(lastName);
        }

        if (organisationName != null) {
            OrganisationModel organisation = OrganisationModel.builder().name(organisationName).build();
            updateBuilder.organisation(organisation);
        }

        if (accountStatusName != null) {
            AccountStatus accountStatus = AccountStatus.valueOf(accountStatusName);
            updateBuilder.accountStatus(accountStatus);

        }

        if (roleName != null) {
            Role role = Role.valueOf(roleName);
            updateBuilder.role(role);
        }

        UserModel updatedUser = updateBuilder.build();
        userValidator.validate(updatedUser);
        userRepository.updateUser(updatedUser);

        log.info("updated user with id=" + userId);
    }

    public List<UserModel> listUsers() {
        return userRepository.listUsers();
    }

    public List<OrganisationModel> listOrganisations() {
        return organisationRepository.listOrganisations();
    }

    public void blockUser(int userId) throws Exception {
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

    public void unblockUser(int userId) throws Exception {
        UserModel user = unsafeFindUserById(userId);

        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
            log.info("User with user.id=" + userId + " is already active.");
            return;
        }

        UserModel unblockedUser = user.toBuilder().accountStatus(AccountStatus.ACTIVE).build();
        userRepository.updateUser(unblockedUser);
        log.info("Unlocked user with user.id=" + userId + ".");
    }

    private UserModel unsafeFindUserById(int userId) throws Exception {
        Optional<UserModel> maybeUser = userRepository.findById(userId);
        if (maybeUser.isPresent()) {
            return maybeUser.get();
        }

        String message = "User with user.id=" + userId + " does not exist.";
        log.severe(message);
        throw new Exception(message);
    }
}