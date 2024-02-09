package works.quiet.db;

import works.quiet.etc.FunctionThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public interface RepositoryQuery<T> {
    List<T> queryMany(
            FunctionThrows<Connection, PreparedStatement, Exception> statement,
            FunctionThrows<ResultSet, T, Exception> mapper
    );

    Optional<T> queryOne(
            FunctionThrows<Connection, PreparedStatement, Exception> statement,
            FunctionThrows<ResultSet, T, Exception> mapper
    );
}
