package works.quiet.db;

import org.intellij.lang.annotations.Language;
import works.quiet.etc.FunctionThrows;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public interface DBInterface {

    <T> T rawQuery(
            FunctionThrows<ResultSet, T, Exception> resultSetMapper,
            @Language("PostgreSQL")
            String query,
            Object... values
    );

    <T> List<T> queryMany(
            FunctionThrows<ResultSet, T, Exception> rowMapper,
            @Language("PostgreSQL")
            String query,
            Object... values
    );

    <T> Optional<T> queryOne(
            FunctionThrows<ResultSet, T, Exception> rowMapper,
            @Language("PostgreSQL")
            String query,
            Object... values
    );

    boolean queryExists(@Language("PostgreSQL") String query, Object... values);

    long queryCount(@Language("PostgreSQL") String query);

    int nextVal(@Language("PostgreSQL") String query);

    int upsert(
            FunctionThrows<ResultSet, Integer, Exception> idMapper,
            @Language("PostgreSQL") String query,
            Object... values
    );

    void delete(@Language("PostgreSQL") String query, Object... values);

    void beginTransaction();
    void commitTransaction();
}
