package works.quiet.db;

public interface MutationHelper {
    int save(boolean omitId, String[] fields, Object[] values);
    void delete(int id);
}
