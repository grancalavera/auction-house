package works.quiet.domain;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    // small i integer is primitive "stack allocated"
    // while Integer is an object reference that needs
    // to be de-ref from the heap 🐢
    private int id;
    // strings are special, there is an allocated but
    // the backing object is stored into a "global pool"
    // of strings, and the JVM does fancy magic to dealloc
    // strings. string dupe is common
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
    private AccountStatus accountStatus;
}
