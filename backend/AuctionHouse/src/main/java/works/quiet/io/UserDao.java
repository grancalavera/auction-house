package works.quiet.io;

import works.quiet.domain.User;
import java.util.Collection;
import java.util.Optional;

public interface UserDao extends AutoCloseable {
    Optional<User> findWithCredentials(String username, String password);
}
