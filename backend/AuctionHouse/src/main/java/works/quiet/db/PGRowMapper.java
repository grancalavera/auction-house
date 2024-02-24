package works.quiet.db;

import java.sql.ResultSet;

public interface PGRowMapper<T> {
    T fromResulSet(ResultSet resultSet) throws Exception;
    T fromResulSet(String fieldPrefix, ResultSet resultSet) throws Exception;
}
