package works.quiet.user;

public class BadLoginException extends Exception{
    public BadLoginException() {
        super("Incorrect username or password.");
    }
}
