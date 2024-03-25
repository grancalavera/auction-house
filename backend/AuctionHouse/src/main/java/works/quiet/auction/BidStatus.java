package works.quiet.auction;

import lombok.Getter;

@Getter
public enum BidStatus {
    PLACED(1),
    NOT_FILLED(2),
    FILLED(3);
    private final int id;

    BidStatus(final int id) {
        this.id = id;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public BidStatus ofInt(final int id) throws IllegalArgumentException {
        switch (id) {
            case 1 -> {
                return BidStatus.PLACED;
            }
            case 2 -> {
                return BidStatus.NOT_FILLED;
            }
            case 3 -> {
                return BidStatus.FILLED;
            }
            default -> throw new IllegalArgumentException(id + " is not a valid ExecutionStatus id.");
        }
    }
}
