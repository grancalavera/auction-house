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
public abstract class PGRepositoryQuery<T> implements RepositoryQuery<T> {
    private final DBConnection connection;

    public PGRepositoryQuery(final Level logLevel, final DBConnection connection, final PGMapper<T> mapper) {
        this.connection = connection;
        log.setLevel(logLevel);
    }

    @Override
    public List<T> queryMany(
            final FunctionThrows<Connection, PreparedStatement, Exception> query,
            final FunctionThrows<ResultSet, T, Exception> mapper
    ) {
        List<T> result = new ArrayList<>();

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

    @Override
    public Optional<T> queryOne(
            final FunctionThrows<Connection, PreparedStatement, Exception> query,
            final FunctionThrows<ResultSet, T, Exception> mapper
    ) {
        List<T> manyResults = queryMany(query, mapper);

        if (manyResults.isEmpty()) {
            log.info("empty");
            return Optional.empty();
        }

        T result = manyResults.getFirst();
        log.info(result.toString());
        return Optional.of(result);
    }
}
