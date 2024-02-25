package works.quiet.db;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    Optional<T> findById(int id);

    List<T> findAll();

    long count();

    boolean exists(int id);

    T save(T entity);

    void delete(T entity);

    int nextId();
}
