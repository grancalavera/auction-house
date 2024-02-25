package works.quiet.db;

import org.intellij.lang.annotations.Language;
import works.quiet.etc.FunctionThrows;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public interface DBInterface {

    <T> T rawQuery(
            @Language("PostgreSQL")
            String query,
            Object[] values,
            FunctionThrows<ResultSet, T, Exception> resultSetMapper
    );

    <T> T rawQuery(
            @Language("PostgreSQL")
            String query,
            FunctionThrows<ResultSet, T, Exception> resultSetMapper
    );

    <T> List<T> queryMany(
            @Language("PostgreSQL")
            String query,
            FunctionThrows<ResultSet, T, Exception> rowMapper
    );

    <T> List<T> queryMany(
            @Language("PostgreSQL")
            String query,
            Object[] values,
            FunctionThrows<ResultSet, T, Exception> rowMapper
    );

    <T> Optional<T> queryOne(
            @Language("PostgreSQL")
            String query,
            Object[] values,
            FunctionThrows<ResultSet, T, Exception> rowMapper
    );

    <T> Optional<T> queryOne(
            @Language("PostgreSQL")
            String query,
            FunctionThrows<ResultSet, T, Exception> rowMapper
    );

    boolean queryExists(@Language("PostgreSQL") String query, Object[] values);

    long queryCount(@Language("PostgreSQL") String query);

    int nextVal(@Language("PostgreSQL") String query);

    int upsert(
            @Language("PostgreSQL") String query,
            Object[] values,
            FunctionThrows<ResultSet, Integer, Exception> idMapper
    );

    void delete(@Language("PostgreSQL") String query, Object[] values);

    int upsertDeprecated(
            String tableName,
            boolean omitId,
            String[] fields,
            Object[] values
    );

    void deleteDeprecated(
            String tableName,
            int id
    );
}
