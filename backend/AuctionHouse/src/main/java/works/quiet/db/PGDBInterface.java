package works.quiet.db;

import lombok.extern.java.Log;
import org.intellij.lang.annotations.Language;
import works.quiet.etc.FunctionThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGDBInterface implements DBInterface {
    private final DBConnection connection;

    public PGDBInterface(final Level logLevel, final DBConnection connection) {
        this.connection = connection;
        log.setLevel(logLevel);
    }

    private Connection getUnsafeConnection() {
        return connection.getConnection().orElseThrow(
                () -> new RuntimeException("SQL: getConnection boom 💥!")
        );
    }

    @Override
    public <T> T rawQuery(
            @Language("PostgreSQL") final String query,
            final FunctionThrows<ResultSet, T, Exception> resultSetMapper
    ) {
        return rawQuery(query, null, resultSetMapper);
    }

    @Override
    public <T> List<T> queryMany(final String query, final FunctionThrows<ResultSet, T, Exception> rowMapper) {
        return queryMany(query, null, rowMapper);
    }

    @Override
    public <T> List<T> queryMany(
            @Language("PostgreSQL") final String query,
            final Object[] values,
            final FunctionThrows<ResultSet, T, Exception> rowMapper
    ) {
        return rawQuery(query, values, resultSet -> {
            var result = new ArrayList<T>();
            while (resultSet.next()) {
                var row = rowMapper.apply(resultSet);
                result.add(row);
            }
            return result;
        });
    }

    public <T> Optional<T> queryOne(
            @Language("PostgreSQL") final String query,
            final Object[] values,
            final FunctionThrows<ResultSet, T, Exception> rowMapper
    ) {
        return rawQuery(query, values, rs -> rs.next() ? Optional.of(rowMapper.apply(rs)) : Optional.empty());
    }

    @Override
    public <T> Optional<T> queryOne(
            @Language("PostgreSQL") final String query,
            final FunctionThrows<ResultSet, T, Exception> rowMapper
    ) {
        return queryOne(query, null, rowMapper);
    }

    @Override
    public <T> T rawQuery(
            @Language("PostgreSQL") final String query,
            final Object[] values,
            final FunctionThrows<ResultSet, T, Exception> resultSetMapper
    ) {
        ResultSet resultSet = null;

        var conn = getUnsafeConnection();

        try (
                var preparedStatement = conn.prepareStatement(query);
        ) {
            if (values != null) {
                setStatementValues(preparedStatement, values);
            }
            resultSet = preparedStatement.executeQuery();
            return resultSetMapper.apply(resultSet);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (final Exception ex) {
                    log.severe("Failed to close ResultSet");
                }
            }

        }
    }

    @Override
    public boolean queryExists(@Language("PostgreSQL") final String query, final Object[] values) {
        return rawQuery(query, values, ResultSet::next);
    }

    @Override
    public long queryCount(@Language("PostgreSQL") final String query) {
        return rawQuery(query, rs -> rs.next() ? rs.getLong("count") : 0);
    }

    @Override
    public int nextVal(final String query) {
        return rawQuery(query, rs -> {
            rs.next();
            return rs.getInt("nextval");
        });
    }

    @Override
    public int upsert(
            @Language("PostgreSQL") final String query,
            final Object[] values,
            final FunctionThrows<ResultSet, Integer, Exception> idMapper
    ) {
        var conn = getUnsafeConnection();
        ResultSet resultSet = null;

        try (var preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            if (values != null) {
                setStatementValues(preparedStatement, values);
            }
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            return idMapper.apply(resultSet);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (final Exception ex) {
                    log.severe("Failed to close ResultSet");
                }
            }
        }
    }

    @Override
    public void delete(final String query, final Object[] values) {
        var conn = getUnsafeConnection();
        try (var preparedStatement = conn.prepareStatement(query)) {
            if (values != null) {
                setStatementValues(preparedStatement, values);
                preparedStatement.executeUpdate();
            }
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // https://stackoverflow.com/a/2563492
    // https://balusc.omnifaces.org/2008/07/dao-tutorial-data-layer.html
    // will extract to somewhere later on...
    private void setStatementValues(final PreparedStatement st, final Object... values) throws SQLException {
        if (values == null) {
            return;
        }

        for (int i = 0; i < values.length; i++) {
            st.setObject(i + 1, values[i]);
        }
    }
}
