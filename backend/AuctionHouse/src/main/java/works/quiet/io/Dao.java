package works.quiet.io;

import works.quiet.etc.FunctionThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public interface Dao<T> {
    List<T> queryMany(
            FunctionThrows<Connection, PreparedStatement, Exception> statement,
            FunctionThrows<ResultSet, T, Exception> mapper
    );
}
