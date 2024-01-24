package works.quiet.user;

import java.util.Optional;

public interface UserRepository {
    Optional<UserModel> findWithCredentials(String username, String password);
    Optional<UserModel> findByUsername(String username);
}
