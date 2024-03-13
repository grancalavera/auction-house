package works.quiet.reports;

import lombok.Getter;

@Getter
public enum ExecutionStatus {
    NOT_FILLED(1),
    FILLED(2);
    private final int id;

    ExecutionStatus(final int id) {
        this.id = id;
    }

    public ExecutionStatus ofInt(final int id) throws IllegalArgumentException {
        switch (id) {
            case 1 -> {
                return ExecutionStatus.NOT_FILLED;
            }
            case 2 -> {
                return ExecutionStatus.FILLED;
            }
            default -> throw new IllegalArgumentException(id + " is not a valid ExecutionStatus id.");
        }
    }
}
