package works.quiet.reference;

import works.quiet.db.PGMapper;

import java.sql.ResultSet;

public class PGOrganisationMapper implements PGMapper<Organisation> {
    @Override
    public Organisation fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public Organisation fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {
        return Organisation
                .builder()
                .id(resultSet.getInt(fieldPrefix + "id"))
                .name(resultSet.getString(fieldPrefix + "name"))
                .build();
    }
}
