package works.quiet.user;

import works.quiet.user.UserModel;

import java.util.Optional;

public interface UserDao {
    Optional<UserModel> findWithCredentials(String username, String password);
}
