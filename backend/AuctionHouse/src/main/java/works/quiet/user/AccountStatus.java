package works.quiet.user;

import lombok.Getter;

@Getter
public enum AccountStatus {
    ACTIVE(1),
    BLOCKED(2);
    private final int id;

    AccountStatus(final int id) {
        this.id = id;
    }

    public static AccountStatus ofInt(final int id) throws IllegalArgumentException {
        switch (id) {
            case 1 -> {
                return AccountStatus.ACTIVE;
            }
            case 2 -> {
                return AccountStatus.BLOCKED;
            }
            default -> throw new IllegalArgumentException(id + " is not a valid AccountStatus id.");
        }
    }
}
