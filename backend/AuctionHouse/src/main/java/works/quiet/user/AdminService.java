package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.reference.Organisation;
import works.quiet.reference.OrganisationRepository;

import java.util.List;
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
            final UserValidator userValidator) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
        this.session = session;
        this.userValidator = userValidator;
        log.setLevel(logLevel);
    }

    public void login(final String username, final String password) {
        var user = userRepository.findWithCredentials(username, password)
                .orElseThrow(() -> new RuntimeException("Incorrect username or password."));
        session.open(user.getUsername());
    }

    public void logout() {
        session.close();
        log.info("Logged out.");
    }

    public void assertIsNotBlocked() {
        User user = getCurrentUser();
        if (user.getAccountStatus() == AccountStatus.BLOCKED) {
            log.severe("Not authorised: username=\"" + user.getUsername() + "\" is blocked.");
            throw new RuntimeException("Not authorised.");
        }
    }

    public void assertIsUser() {
        if (getCurrentUserRole() != Role.USER) {
            throw new RuntimeException("Not an user.");
        }
    }

    public void assertIsAdmin() {
        if (getCurrentUserRole() != Role.ADMIN) {
            log.severe("Current user is not an admin, current role=" + getCurrentUserRole());
            throw new RuntimeException("Not an admin.");
        }
    }

    public String getCurrentUsername() {
        return session.getUsername().orElseThrow(() -> new RuntimeException("Not authenticated."));
    }

    public User getCurrentUser() {
        var username = getCurrentUsername();
        var user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new RuntimeException("User with username=\"" + username + "\" does not exist."));
    }

    private Role getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    public Organisation findOrganisationByName(final String name) {
        return organisationRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Organisation with name=\"" + name + "\" does not exist."));
    }

    public int createUser(final User user) {
        userValidator.validate(user);
        User created = null;
        try {
            created = userRepository.save(user);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to create user. Please try again.");
        }
        var id = created.getId();
        log.info("created user with id=" + id);
        return id;
    }

    public void updateUser(final User updatedUser) {
        userValidator.validate(updatedUser);

        try {
            userRepository.save(updatedUser);
        } catch (final Exception e) {
            throw new RuntimeException("User update failed, please try again.");
        }

        log.info("updated user with id=" + updatedUser.getId());
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public List<Organisation> listOrganisations() {
        return organisationRepository.findAll();
    }

    public void blockUser(final int userId) {
        User user = findUserById(userId);

        if (user.getId() == getCurrentUser().getId()) {
            throw new RuntimeException("Cannot block current user.");
        }

        if (user.getAccountStatus() == AccountStatus.BLOCKED) {
            log.info("User with user.id=" + userId + " is already blocked.");
            return;
        }

        User blockedUser = user.toBuilder().accountStatus(AccountStatus.BLOCKED).build();
        try {
            userRepository.save(blockedUser);
            log.info("Blocked user with user.id=" + userId + ".");
        } catch (final Exception e) {
            throw new RuntimeException("Failed to block user with user.id=" + userId + ". Please try again.");
        }
    }

    public void unblockUser(final int userId) {
        User user = findUserById(userId);

        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
            log.info("User with user.id=" + userId + " is already active.");
            return;
        }

        User unblockedUser = user.toBuilder().accountStatus(AccountStatus.ACTIVE).build();

        try {
            userRepository.save(unblockedUser);
            log.info("Unlocked user with user.id=" + userId + ".");
        } catch (final Exception e) {
            throw new RuntimeException("Failed to unblock user with user.id=" + userId + ". Please try again.");
        }
    }

    public void deleteUserById(final int userId) {
        try {
            final var user = findUserById(userId);
            userRepository.delete(user);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to delete User with id=" + userId + ". Please try again.");
        }
    }

    public long countUsers() {
        return userRepository.count();
    }

    public boolean userExists(final int id) {
        return userRepository.exists(id);
    }

    public User findUserById(final int userId) {
        return userRepository.findOne(userId)
                .orElseThrow(() -> new RuntimeException("User with user.id=" + userId + " does not exist."));
    }
}
