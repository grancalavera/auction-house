package works.quiet.dao;

import java.sql.ResultSet;

public interface PGDaoMapper<T> {
    T map(ResultSet resultSet) throws Exception;
}
