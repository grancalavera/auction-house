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
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Log
public class PGDBInterface implements DBInterface {
    private final DBConnection connection;

    public PGDBInterface(final Level logLevel, final DBConnection connection) {
        this.connection = connection;
        log.setLevel(logLevel);
    }

    @Override
    public <T> T rawQuery_deprecated(
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
    public <T> T rawQuery(
            @Language("PostgreSQL") final String query,
            final Object[] values,
            final FunctionThrows<ResultSet, T, Exception> resultSetMapper
    ) {
        ResultSet resultSet = null;

        var conn = connection.getConnection().orElseThrow(
                () -> new RuntimeException("SQL: getConnection boom 💥!")
        );

        try (
                var preparedStatement = conn.prepareStatement(query);
        ) {
            setStatementValues(preparedStatement, values);
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
    public <T> List<T> queryMany(
            final FunctionThrows<Connection, PreparedStatement, Exception> query,
            final FunctionThrows<ResultSet, T, Exception> rowMapper
    ) {
        return rawQuery_deprecated(query, resultSet -> {
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
        return rawQuery_deprecated(query, rs -> rs.next() ? Optional.of(rowMapper.apply(rs)) : Optional.empty());
    }

    @Override
    public boolean queryExists(@Language("PostgreSQL") final String query, final Object[] values) {
        return rawQuery(query, values, ResultSet::next);
    }

    @Override
    public long queryCount(final FunctionThrows<Connection, PreparedStatement, Exception> query) {
        return rawQuery_deprecated(query, rs -> rs.next() ? rs.getLong("count") : 0);
    }

    @Override
    public int upsert(final String tableName, final boolean omitId, final String[] fields, final Object[] values) {
        AtomicReference<Integer> idRef = new AtomicReference<>();
        var helper = new UpdateFieldsAndValuesHelper(omitId, fields, values);

        var sql = "INSERT INTO " + tableName + " (" + helper.getFieldNames() + ")"
                + " VALUES (" + helper.getValuePlaceholders() + ")"
                + " ON CONFLICT (id)"
                + " DO UPDATE SET"
                + " " + helper.getConflictResolution();

        connection.getConnection().ifPresent(conn -> {
            try (var st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                var stValues = helper.getValues();
                setStatementValues(st, stValues);
                st.executeUpdate();
                var rs = st.getGeneratedKeys();
                rs.next();
                var id = rs.getInt("id");
                idRef.set(id);
            } catch (final SQLException ex) {
                log.severe(ex.toString());
                throw new RuntimeException(ex);
            }
        });

        return idRef.get();
    }

    @Override
    public void delete(final String tableName, final int id) {
        var sql = "DELETE FROM " + tableName + " WHERE id=?";
        connection.getConnection().ifPresent(conn -> {
            try (
                    PreparedStatement st = conn.prepareStatement(sql);
            ) {
                st.setInt(1, id);
                st.executeUpdate();
            } catch (final SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
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
