package works.quiet.db;

public interface MutationHelper {
    int save(String tableName, boolean omitId, String[] fields, Object[] values);
    void delete(String tableName, int id);
}
