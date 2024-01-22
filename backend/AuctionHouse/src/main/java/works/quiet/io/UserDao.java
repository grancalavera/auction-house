package works.quiet.io;

import works.quiet.domain.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class UserDao implements Dao<User, Integer>{
    @Override
    public Optional<User> get(int id) {
        return Optional.empty();
    }

    @Override
    public Collection<User> getAll() {
        return new ArrayList<User>();
    }

    @Override
    public Optional<Integer> save(User user) {
        return Optional.empty();
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(User user) {

    }
}
