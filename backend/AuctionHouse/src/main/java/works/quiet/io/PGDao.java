package works.quiet.io;

import lombok.extern.java.Log;
import works.quiet.etc.FunctionThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Log
public abstract class PGDao<T> implements Dao<T> {
    private DBConnection connection = null;

    public PGDao(Level logLevel, DBConnection connection) {
        this.connection = connection;
        log.setLevel(logLevel);
    }


    @Override
    public List<T> queryMany(
            FunctionThrows<Connection, PreparedStatement, Exception> statement,
            FunctionThrows<ResultSet, T, Exception> mapper
    ) {
        List<T> result = new ArrayList<>();
        connection.getConnection().ifPresent(conn -> {
            try (PreparedStatement st = statement.apply(conn); ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.apply(rs));
                }
            } catch (Exception ex) {
                log.severe(ex.toString());
            }
        });

        log.info(result.toString());
        return result;
    }

}
