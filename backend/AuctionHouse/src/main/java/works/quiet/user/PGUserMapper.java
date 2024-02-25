package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.db.PGMapper;
import works.quiet.reference.Organisation;

import java.sql.ResultSet;
import java.util.logging.Level;

@Log
public class PGUserMapper implements PGMapper<User> {
    public PGUserMapper(final Level logLevel) {
        log.setLevel(logLevel);
    }

    @Override
    public User fromResulSet(final ResultSet resultSet) throws Exception {
        return fromResulSet("", resultSet);
    }

    @Override
    public User fromResulSet(final String fieldPrefix, final ResultSet resultSet) throws Exception {
        var organisation = Organisation
                .builder()
                .id(resultSet.getInt(fieldPrefix + "organisationId"))
                .name(resultSet.getString(fieldPrefix + "organisation"))
                .build();

        var role = Role
                .valueOf(resultSet.getString(fieldPrefix + "role"));

        var accountStatus = AccountStatus
                .valueOf(resultSet.getString(fieldPrefix + "accountStatus"));

        return User.builder()
                .id(resultSet.getInt(fieldPrefix + "id"))
                .username(resultSet.getString(fieldPrefix + "username"))
                .password(resultSet.getString(fieldPrefix + "password"))
                .firstName(resultSet.getString(fieldPrefix + "firstName"))
                .lastName(resultSet.getString(fieldPrefix + "lastName"))
                .accountStatus(accountStatus)
                .role(role)
                .organisation(organisation)
                .build();
    }
}
