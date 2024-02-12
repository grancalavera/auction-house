package works.quiet.user;

import java.util.Optional;

public interface Session {
    void open(String username);
    void close();
    Optional<String> getUsername();
}
