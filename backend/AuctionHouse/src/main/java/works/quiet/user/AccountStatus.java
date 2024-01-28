package works.quiet.user;

import lombok.Getter;
import org.w3c.dom.ranges.RangeException;

@Getter
public enum AccountStatus {
    ACTIVE(1),
    BLOCKED(2);
    private final int id;
    AccountStatus(int id) {
        this.id = id;
    }

    public static AccountStatus ofInt(int id) throws  IllegalArgumentException {
        AccountStatus status;
        switch (id) {
            case 1 -> status = AccountStatus.ACTIVE;
            case 2 -> status = AccountStatus.BLOCKED;
            default -> throw new IllegalArgumentException(id + " is not a valid AccountStatus id.");
        }
        return status;
    }
}
