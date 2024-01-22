package works.quiet.domain;

public record UserTwo(int id,
                      String username,
                      String password,
                      String firstName,
                      String lastName,
                      Role role,
                      AccountStatus accountStatus) {

}
