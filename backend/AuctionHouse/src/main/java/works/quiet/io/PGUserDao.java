package works.quiet.io;

import works.quiet.domain.User;

import java.util.Optional;

public class PGUserDao implements UserDao{
    @Override
    public Optional<User> findWithCredentials(String username, String password) {
        return Optional.empty();
    }
}
