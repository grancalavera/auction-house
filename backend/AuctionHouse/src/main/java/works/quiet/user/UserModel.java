package works.quiet.user;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class UserModel {
    // small i integer is primitive "stack allocated"
    // while Integer is an object reference that needs
    // to be de-ref from the heap 🐢
    private int id;
    // strings are special, there is an allocated part but
    // the backing object is stored into a "global pool"
    // of strings, and the JVM does fancy magic to dealloc
    // strings. string dupe is common
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private OrganisationModel organisation;
    @Builder.Default private Role role = Role.USER;
    @Builder.Default private AccountStatus accountStatus = AccountStatus.ACTIVE;
}
