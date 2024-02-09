package works.quiet.user;

import works.quiet.db.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User> {
    Optional<User> findWithCredentials(String username, String password);

    Optional<User> findByUsername(String username);
}
