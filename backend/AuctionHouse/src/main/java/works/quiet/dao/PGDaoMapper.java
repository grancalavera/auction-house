package works.quiet.dao;

import java.sql.ResultSet;

public interface PGDaoMapper<T> {
    T fromResulSet(ResultSet resultSet) throws Exception;
}
