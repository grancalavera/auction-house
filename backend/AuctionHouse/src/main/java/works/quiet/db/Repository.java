package works.quiet.db;

import works.quiet.user.User;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    Optional<T> findOne(int id);

    List<T> findAll();

//    Long count();

//    boolean exists(int id);

    User save(T user) throws Exception;

    //    void delete(User user);
}
