package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.etc.Validator;

import java.util.logging.Level;
import java.util.regex.Pattern;

@Log
public class UserValidator implements Validator<User> {

    private static final int MIN_PASSWORD_LENGTH = 3;

    public UserValidator(final Level logLevel) {
        log.setLevel(logLevel);
    }

    @Override
    public void validate(final User user) {
        validateUsername(user.getUsername());
        validatePassword(user.getPassword());
    }

    private void validateUsername(final String username) {
        String usernamePattern = "^[a-zA-Z0-9]+$";
        final boolean matches = Pattern.matches(usernamePattern, username);
        if (!matches) {
            throw new RuntimeException("Invalid username: only alpha-numerical characters allowed.");
        }
    }

    private void validatePassword(final String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new RuntimeException("Invalid password: password must be at least "
                    + MIN_PASSWORD_LENGTH
                    + " characters long");
        }
    }
}
