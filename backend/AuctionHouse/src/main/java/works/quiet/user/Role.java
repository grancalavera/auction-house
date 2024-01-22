package works.quiet.user;

public enum Role {
    ADMIN(1),
    USER(2);
    final int id;
    Role(int id) {
        this.id = id;
    }

    public static Role ofInt(int id) throws IllegalArgumentException {
        Role role;
        switch(id) {
            case 1 -> role = Role.ADMIN;
            case 2 -> role = Role.USER;
            default -> throw new IllegalArgumentException(id + " is not a valid Role id.");
        }
        return role;
    }
}
