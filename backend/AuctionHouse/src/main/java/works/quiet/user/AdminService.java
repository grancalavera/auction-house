package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.reference.Organisation;
import works.quiet.reference.OrganisationRepository;
import works.quiet.resources.Resources;

import java.util.List;
import java.util.logging.Level;

@Log
public class AdminService {
    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final Session session;
    private final UserValidator userValidator;
    private final Resources resources;
    private String currentUsername;
    private User currentUser;

    public AdminService(
            final Level logLevel,
            final Resources resources,
            final UserRepository userRepository,
            final OrganisationRepository organisationRepository,
            final Session session,
            final UserValidator userValidator) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
        this.session = session;
        this.userValidator = userValidator;
        this.resources = resources;
        log.setLevel(logLevel);
    }

    public void login(final String username, final String password) {
        var user = userRepository.findWithCredentials(username, password)
                .orElseThrow(() -> new RuntimeException(resources.getString("errors.badLogin")));
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
            throw new RuntimeException(resources.getString("errors.notAuthorised"));
        }
    }

    public void assertIsUser() {
        if (getCurrentUserRole() != Role.USER) {
            throw new RuntimeException(resources.getString("errors.badRoleNotUser"));
        }
    }

    public void assertIsAdmin() {
        if (getCurrentUserRole() != Role.ADMIN) {
            log.severe("Current user is not an admin, current role=" + getCurrentUserRole());
            throw new RuntimeException(resources.getString("errors.badRoleNotAdmin"));
        }
    }

    public String getCurrentUsername() {
        if (currentUsername == null) {
            currentUsername =
                    session.getUsername().orElseThrow(() ->
                            new RuntimeException(resources.getString("errors.notAuthenticated")));
        }
        return currentUsername;
    }

    public User getCurrentUser() {
        if (currentUsername == null) {
            var username = getCurrentUsername();
            var user = userRepository.findByUsername(username);
            currentUser = user.orElseThrow(() ->
                    new RuntimeException(resources.getFormattedString("errors.badUsername", username)));
        }
        return currentUser;
    }

    private Role getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    public Organisation findOrganisationByName(final String name) {
        return organisationRepository.findByName(name).orElseThrow(() ->
                new RuntimeException(resources.getFormattedString("errors.badOrganisationName", name)));
    }

    public User createUser(final User user) {
        userValidator.validate(user);
        var created = userRepository.save(user);
        log.info("created user with id=" + created.getId());
        return created;
    }

    public User updateUser(final User user) {
        userValidator.validate(user);
        var updated = userRepository.save(user);
        log.info("updated user with id=" + updated.getId());
        return updated;
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
            throw new RuntimeException(resources.getString("errors.cannotBlockCurrentUser"));
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
            throw new RuntimeException(resources.getFormattedString("errors.blockUserFailed", userId));
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
            throw new RuntimeException(resources.getFormattedString("errors.unblockUserFailed", userId));
        }
    }

    public void deleteUserById(final int userId) {
        try {
            final var user = findUserById(userId);
            userRepository.delete(user);
        } catch (final Exception e) {
            throw new RuntimeException(resources.getFormattedString("errors.deleteUserFailed", userId));
        }
    }

    public long countUsers() {
        return userRepository.count();
    }

    public boolean userExists(final int id) {
        return userRepository.exists(id);
    }

    public User findUserById(final int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(resources.getFormattedString("errors.badUserId", userId)));
    }
}
