package works.quiet.user;

import lombok.extern.java.Log;
import works.quiet.etc.Validator;
import works.quiet.resources.Resources;

import java.util.logging.Level;
import java.util.regex.Pattern;

@Log
public class UserValidator implements Validator<User> {

    private static final int MIN_PASSWORD_LENGTH = 3;
    private final Resources resources;

    public UserValidator(final Level logLevel, final Resources resources) {
        this.resources = resources;
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
            throw new RuntimeException(resources.getString("errors.invalidUsername"));
        }
    }

    private void validatePassword(final String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new RuntimeException(resources.getFormattedString("errors.invalidPassword", MIN_PASSWORD_LENGTH));
        }
    }
}
