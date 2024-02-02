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
import java.util.concurrent.atomic.AtomicReference;
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
    public List<T> queryMany(FunctionThrows<Connection, PreparedStatement, Exception> query) {
        List<T> result = new ArrayList<>();

        connection.getConnection().ifPresent(conn -> {
            try (PreparedStatement st = query.apply(conn); ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.fromResulSet(rs));
                }
            } catch (Exception ex) {
                log.severe(ex.toString());
            }
        });

        log.info(result.toString());
        return result;
    }

    @Override
    public Optional<T> queryOne(FunctionThrows<Connection, PreparedStatement, Exception> query) {
        List<T> manyResults = queryMany(query);

        if (manyResults.isEmpty()) {
            log.info("empty");
            return Optional.empty();
        }

        T result = manyResults.getFirst();
        log.info(result.toString());
        return Optional.of(result);
    }

    @Override
    public int createOne(
            String idKey,
            FunctionThrows<Connection, PreparedStatement, Exception> mutation
    ) throws Exception {
        AtomicReference<Integer> idRef = new AtomicReference<>();
        var conn = connection.getConnection().get(); // 💥

        try (PreparedStatement st = mutation.apply(conn)) {
            int rowsInserted = st.executeUpdate();
            log.info(rowsInserted + " rows inserted.");

            try (ResultSet rs = st.getGeneratedKeys()) {
                rs.next();
                var key = rs.getInt("id");
                idRef.set(key);
            } catch (Exception ex) {
                log.severe(ex.toString());
                throw ex;
            }

        } catch (Exception ex) {
            log.severe(ex.toString());
            throw ex;
        }

        return idRef.get().intValue();
    }
}
