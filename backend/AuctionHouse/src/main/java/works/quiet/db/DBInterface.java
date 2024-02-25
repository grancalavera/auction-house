package works.quiet.db;

import org.intellij.lang.annotations.Language;
import works.quiet.etc.FunctionThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    @SuppressWarnings("checkstyle:MethodName")
    <T> T rawQuery_deprecated(
            FunctionThrows<Connection, PreparedStatement, Exception> query,
            FunctionThrows<ResultSet, T, Exception> resultSetMapper
    );

    boolean queryExists(@Language("PostgreSQL") String query, Object[] values);

    long queryCount(@Language("PostgreSQL") String query);

    int upsert(
            String tableName,
            boolean omitId,
            String[] fields,
            Object[] values
    );

    void delete(
            String tableName,
            int id
    );
}
