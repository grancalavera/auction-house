package works.quiet.db;

public interface IdSource<T> {
    int generateId(T entity);
}
