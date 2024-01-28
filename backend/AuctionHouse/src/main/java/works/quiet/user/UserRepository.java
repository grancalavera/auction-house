package works.quiet.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<UserModel> listUsers();

    Optional<UserModel> findWithCredentials(String username, String password);

    Optional<UserModel> findByUsername(String username);

    Optional<Integer> createUser(UserModel prototype);
}
