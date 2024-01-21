package works.quiet.domain;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Integer id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
    private AccountStatus accountStatus;
}
