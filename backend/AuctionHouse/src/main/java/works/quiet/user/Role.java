package works.quiet.user;

import lombok.Getter;

@Getter
public enum Role {
    USER(1),
    ADMIN(2);
    private final int id;

    Role(final int id) {
        this.id = id;
    }

    public static Role ofInt(final int id) throws IllegalArgumentException {
        switch (id) {
            case 1 -> {
                return Role.USER;
            }
            case 2 -> {
                return Role.ADMIN;
            }
            default -> throw new IllegalArgumentException(id + " is not a valid Role id.");
        }
    }
}
