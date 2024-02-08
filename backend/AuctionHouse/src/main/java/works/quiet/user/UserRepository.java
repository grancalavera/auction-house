package works.quiet.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserModel> findOne(int id);

    List<UserModel> findAll();

    UserModel save(UserModel user) throws Exception;

    Optional<UserModel> findWithCredentials(String username, String password);

    Optional<UserModel> findByUsername(String username);

//    Long count();
//    void delete(UserModel user);
//    boolean exists(int id);
}
