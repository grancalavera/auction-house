package works.quiet.db;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    Optional<T> findOne(int id);

    List<T> findAll();

//    Long count();

    boolean exists(int id);

    T save(T entity) throws Exception;

    void delete(T entity) throws Exception;
}
