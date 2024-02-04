package works.quiet.user;

import lombok.Getter;

@Getter
public enum Role {
    USER(1),
    ADMIN(2);
    private final int id;
    Role(int id) {
        this.id = id;
    }

    public static Role ofInt(int id) throws IllegalArgumentException {
        Role role;
        switch(id) {
            case 1 -> role = Role.USER;
            case 2 -> role = Role.ADMIN;
            default -> throw new IllegalArgumentException(id + " is not a valid Role id.");
        }
        return role;
    }
}
