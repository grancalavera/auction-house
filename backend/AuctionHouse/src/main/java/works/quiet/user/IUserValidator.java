package works.quiet.user;

public interface IUserValidator {
    void validateUsername(String username) throws Exception;

    void validatePassword(String password) throws Exception;
}

