package works.quiet.dao;

import lombok.extern.java.Log;
import works.quiet.reference.OrganisationModel;
import works.quiet.user.AccountStatus;
import works.quiet.user.Role;
import works.quiet.user.UserModel;

import java.sql.ResultSet;
import java.util.logging.Level;

@Log
public class PGUserDaoMapper implements PGDaoMapper<UserModel> {
    public PGUserDaoMapper(final Level logLevel) {
        log.setLevel(logLevel);
    }

    @Override
    public UserModel fromResulSet(final ResultSet resultSet) throws Exception {
        var organisation = OrganisationModel.builder()
                .id(resultSet.getInt("organisation_id"))
                .name(resultSet.getString("organisation"))
                .build();

        return UserModel.builder()
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
