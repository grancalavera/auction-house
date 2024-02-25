package works.quiet.db;

import works.quiet.etc.FunctionThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public interface DBInterface {
    <T> List<T> queryMany(
            FunctionThrows<Connection, PreparedStatement, Exception> query,
            FunctionThrows<ResultSet, T, Exception> rowMapper
    );

    <T> Optional<T> queryOne(
            FunctionThrows<Connection, PreparedStatement, Exception> query,
            FunctionThrows<ResultSet, T, Exception> rowMapper
    );

    <T> T rawQuery(
            FunctionThrows<Connection, PreparedStatement, Exception> query,
            FunctionThrows<ResultSet, T, Exception> resultSetMapper
    );

    boolean queryExists(FunctionThrows<Connection, PreparedStatement, Exception> query);

    long queryCount(FunctionThrows<Connection, PreparedStatement, Exception> query);

    int upsert(String tableName, boolean omitId, String[] fields, Object[] values);

    void delete(String tableName, int id);
}
