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
        var organisation = Organisation.builder()
                .id(resultSet.getInt("organisation_id"))
                .name(resultSet.getString("organisation"))
                .build();

        Role role = Role.valueOf(resultSet.getString("role"));

        AccountStatus accountStatus = AccountStatus.valueOf(resultSet.getString("account_status"));

        return User.builder()
                .id(resultSet.getInt("id"))
                .username(resultSet.getString("username"))
                .password(resultSet.getString("password"))
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .accountStatus(AccountStatus.valueOf(resultSet.getString("account_status")))
                .role(Role.valueOf(resultSet.getString("role")))
                .organisation(organisation)
                .build();
    }
}
