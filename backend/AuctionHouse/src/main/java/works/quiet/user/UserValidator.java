package works.quiet.user;

import java.util.regex.Pattern;

public class UserValidator implements IUserValidator {
    private final String usernamePattern = "^[a-zA-Z0-9]+$";
    private final int MIN_PASSWORD_LENGTH = 6;

    @Override
    public void validateUsername(String username) throws Exception {
        final boolean matches = Pattern.matches(usernamePattern, username);
        if (!matches) {
            throw new Exception("Invalid username: only alpha-numerical characters allowed.");
        }
    }

    @Override
    public void validatePassword(String password) throws Exception {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new Exception("Invalid password: password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
    }
}
