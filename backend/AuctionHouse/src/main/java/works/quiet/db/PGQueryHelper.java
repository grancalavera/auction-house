package works.quiet.db;

import lombok.extern.java.Log;
import works.quiet.etc.FunctionThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGQueryHelper implements QueryHelper {
    private final DBConnection connection;

    public PGQueryHelper(final Level logLevel, final DBConnection connection) {
        this.connection = connection;
        log.setLevel(logLevel);
    }

    @Override
    public <T> T rawQuery(
            final FunctionThrows<Connection, PreparedStatement, Exception> query,
            final FunctionThrows<ResultSet, T, Exception> resultSetMapper
    ) {
        var conn = connection.getConnection().orElseThrow(
                () -> new RuntimeException("SQL: getConnection boom 💥!")
        );

        try (
                var preparedStatement = query.apply(conn);
                var resultSet = preparedStatement.executeQuery()
        ) {
            return resultSetMapper.apply(resultSet);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public <T> List<T> queryMany(
            final FunctionThrows<Connection, PreparedStatement, Exception> query,
            final FunctionThrows<ResultSet, T, Exception> rowMapper
    ) {
        return rawQuery(query, resultSet -> {
            var result = new ArrayList<T>();
            while (resultSet.next()) {
                var row = rowMapper.apply(resultSet);
                result.add(row);
            }
            return result;
        });
    }

    @Override
    public <T> Optional<T> queryOne(
            final FunctionThrows<Connection, PreparedStatement, Exception> query,
            final FunctionThrows<ResultSet, T, Exception> rowMapper
    ) {
        return rawQuery(query, rs -> rs.next() ? Optional.of(rowMapper.apply(rs)) : Optional.empty());
    }

    @Override
    public boolean queryExists(final FunctionThrows<Connection, PreparedStatement, Exception> query) {
        return queryOne(query,
                rs -> rs.next() ? Optional.of(rs.getInt("id")) : Optional.empty()
        ).isPresent();
    }

    @Override
    public long queryCount(final FunctionThrows<Connection, PreparedStatement, Exception> query) {
        return rawQuery(query, rs -> rs.next() ? rs.getLong("count") : 0);
    }
}
