package works.quiet.db;

import lombok.extern.java.Log;
import works.quiet.etc.FunctionThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public abstract class PGRepositoryQuery<T> implements RepositoryQuery<T> {
    private final DBConnection connection;

    public PGRepositoryQuery(final Level logLevel, final DBConnection connection) {
        this.connection = connection;
        log.setLevel(logLevel);
    }

    @Override
    public List<T> queryMany(
            final FunctionThrows<Connection, PreparedStatement, Exception> query,
            final FunctionThrows<ResultSet, T, Exception> mapper
    ) {
        List<T> result = genericQueryMany(query, mapper);
        log.info(result.toString());
        return result;
    }

    @Override
    public Optional<T> queryOne(
            final FunctionThrows<Connection, PreparedStatement, Exception> query,
            final FunctionThrows<ResultSet, T, Exception> mapper
    ) {
        Optional<T> result = genericQueryOne(query, mapper);
        log.info(result.toString());
        return result;
    }

    @Override
    public boolean queryExists(final FunctionThrows<Connection, PreparedStatement, Exception> query) {
        return genericQueryOne(query, rs ->
                // I know in this project all primary keys are int and live in a column named "id" so 🔥...
                rs.getInt("id")
        ).isPresent();
    }

    @Override
    public long queryCount(final FunctionThrows<Connection, PreparedStatement, Exception> query) {
        var maybeCount = genericQueryOne(query, rs -> rs.getLong("count"));

        if (maybeCount.isPresent()) {
            return maybeCount.get();
        }

        return 0;
    }

    // https://stackoverflow.com/a/2563492
    // https://balusc.omnifaces.org/2008/07/dao-tutorial-data-layer.html
    // will extract to somewhere later on...
    @Override
    public void setStatementValues(final PreparedStatement st, final Object... values) throws SQLException {
        if (values == null) {
            return;
        }

        for (int i = 0; i < values.length; i++) {
            st.setObject(i + 1, values[i]);
        }
    }

    private <U> List<U> genericQueryMany(
            final FunctionThrows<Connection, PreparedStatement, Exception> query,
            final FunctionThrows<ResultSet, U, Exception> mapper
    ) {
        List<U> result = new ArrayList<>();

        connection.getConnection().ifPresent(conn -> {
            try (PreparedStatement st = query.apply(conn); ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.apply(rs));
                }
            } catch (final Exception ex) {
                log.severe(ex.toString());
            }
        });

        log.info(result.toString());
        return result;
    }

    private <U> Optional<U> genericQueryOne(
            final FunctionThrows<Connection, PreparedStatement, Exception> query,
            final FunctionThrows<ResultSet, U, Exception> mapper
    ) {
        List<U> manyResults = genericQueryMany(query, mapper);

        if (manyResults.isEmpty()) {
            log.info("empty");
            return Optional.empty();
        }

        U result = manyResults.getFirst();
        log.info(result.toString());
        return Optional.of(result);
    }
}
