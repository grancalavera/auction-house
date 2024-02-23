package works.quiet.db;

import lombok.extern.java.Log;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;


@Log
public class PGMutationHelper implements MutationHelper {
    private final DBConnection connection;

    public PGMutationHelper(final Level logLevel, final DBConnection connection) {
        log.setLevel(logLevel);
        this.connection = connection;
    }

    @Override
    public int save(final String tableName, final boolean omitId, final String[] fields, final Object[] values) {
        AtomicReference<Integer> idRef = new AtomicReference<>();
        var helper = new UpdateFieldsAndValuesHelper(omitId, fields, values);

        String sql = "INSERT INTO " + tableName + " (" + helper.getFieldNames() + ")"
                + " VALUES (" + helper.getValuePlaceholders() + ")"
                + " ON CONFLICT (id)"
                + " DO UPDATE SET"
                + " " + helper.getConflictResolution();

        connection.getConnection().ifPresent(conn -> {
            try (var st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setStatementValues(st, helper.getValues());
                st.executeUpdate();
                var rs = st.getGeneratedKeys();
                var id = rs.getInt("id");
                idRef.set(id);
            } catch (final SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        return idRef.get();
    }

    @Override
    public void delete(final String tableName, final int id) {
        connection.getConnection().ifPresent(conn -> {
            try (
                    PreparedStatement st = conn.prepareStatement("DELETE FROM " + tableName + " WHERE id=?");
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
