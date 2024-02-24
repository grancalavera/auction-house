package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.db.PGRowMapper;
import works.quiet.reference.Organisation;

import java.sql.ResultSet;
import java.util.logging.Level;

@Log
public class PGUserMapper implements PGRowMapper<User> {
    public PGUserMapper(final Level logLevel) {
        log.setLevel(logLevel);
    }

    @Override
    public User fromResulSet(final ResultSet resultSet) throws Exception {
        var organisation = Organisation
                .builder()
                .id(resultSet.getInt("organisationId"))
                .name(resultSet.getString("organisation"))
                .build();
        var role = Role
                .valueOf(resultSet.getString("role"));
        var accountStatus = AccountStatus
                .valueOf(resultSet.getString("accountStatus"));

        return User.builder()
                .id(resultSet.getInt("id"))
                .username(resultSet.getString("username"))
                .password(resultSet.getString("password"))
                .firstName(resultSet.getString("firstName"))
                .lastName(resultSet.getString("lastName"))
                .accountStatus(accountStatus)
                .role(role)
                .organisation(organisation)
                .build();
    }
}
