package works.quiet.user;

import java.util.Optional;

public interface Session {
    void open(String username) throws Exception;
    void close() throws Exception;
    Optional<String> getUsername();
}
