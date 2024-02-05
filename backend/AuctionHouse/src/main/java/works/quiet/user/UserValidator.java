package works.quiet.user;

import lombok.extern.java.Log;

import java.util.logging.Level;
import java.util.regex.Pattern;

@Log
public class UserValidator implements IUserValidator {

    private static final int MIN_PASSWORD_LENGTH = 3;

    public UserValidator(final Level logLevel) {
        log.setLevel(logLevel);
    }

    @Override
    public void validate(final UserModel user) throws Exception {
        validateUsername(user.getUsername());
        validatePassword(user.getPassword());
    }

    private void validateUsername(final String username) throws Exception {
        String usernamePattern = "^[a-zA-Z0-9]+$";
        final boolean matches = Pattern.matches(usernamePattern, username);
        if (!matches) {
            throw new Exception("Invalid username: only alpha-numerical characters allowed.");
        }
    }

    private void validatePassword(final String password) throws Exception {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new Exception("Invalid password: password must be at least "
                    + MIN_PASSWORD_LENGTH
                    + " characters long");
        }
    }
}
