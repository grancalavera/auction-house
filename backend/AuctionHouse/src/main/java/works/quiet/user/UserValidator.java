package works.quiet.user;

import java.util.regex.Pattern;

public class UserValidator {
    private final String usernamePattern = "^[a-zA-Z0-9]+$";
    private final int MIN_PASSWORD_LENGTH = 16;

    public void validateUsername(String username) throws Exception {
        final boolean matches = Pattern.matches(usernamePattern, username);
        if (!matches) {
            throw new Exception("Invalid username: only alpha-numerical characters allowed.");
        }
    }

    public void validatePassword(String password) throws Exception {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new Exception("Invalid password: password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
    }
}
