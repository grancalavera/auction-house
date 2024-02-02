package works.quiet.dao;

import lombok.extern.java.Log;
import works.quiet.etc.FunctionThrows;
import works.quiet.io.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public abstract class PGDao<T> implements Dao<T> {
    private DBConnection connection;
    private PGDaoMapper<T> mapper;

    public PGDao(Level logLevel, DBConnection connection, PGDaoMapper<T> mapper) {
        this.connection = connection;
        this.mapper = mapper;
        log.setLevel(logLevel);
    }

    @Override
    public List<T> queryMany(FunctionThrows<Connection, PreparedStatement, Exception> statement) {
        List<T> result = new ArrayList<>();

        connection.getConnection().ifPresent(conn -> {
            try (PreparedStatement st = statement.apply(conn); ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.map(rs));
                }
            } catch (Exception ex) {
                log.severe(ex.toString());
            }
        });

        log.info(result.toString());
        return result;
    }

    @Override
    public Optional<T> queryOne(FunctionThrows<Connection, PreparedStatement, Exception> statement) {
        List<T> manyResults = queryMany(statement);

        if (manyResults.isEmpty()) {
            log.info("empty");
            return Optional.empty();
        }

        T result = manyResults.getFirst();
        log.info(result.toString());
        return Optional.of(result);
    }
}
