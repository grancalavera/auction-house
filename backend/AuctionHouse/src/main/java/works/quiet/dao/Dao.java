package works.quiet.dao;

import works.quiet.etc.FunctionThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    List<T> queryMany(
            FunctionThrows<Connection, PreparedStatement, Exception> statement
    );

    Optional<T> queryOne(
            FunctionThrows<Connection, PreparedStatement, Exception> statement
    );

//    int create(
//            String idKey,
//            FunctionThrows<Connection, PreparedStatement, Exception> statement
//    );
}
