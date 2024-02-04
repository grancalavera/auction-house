package works.quiet.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<UserModel> listUsers();

    Optional<UserModel> findWithCredentials(String username, String password);

    Optional<UserModel> findByUsername(String username);

    Optional<UserModel> findById(int id);

    int createUser(
            String username,
            String password,
            String firstName,
            String lastName,
            String organisationName,
            int roleId,
            int accountStatusId
    ) throws Exception;

    void updateUser(UserModel user) throws Exception;
}
