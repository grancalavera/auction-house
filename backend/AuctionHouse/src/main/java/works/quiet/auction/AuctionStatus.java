package works.quiet.auction;

import lombok.Getter;

@Getter
public enum AuctionStatus {
    OPEN(1),
    CLOSED(2);
    private final int id;

    AuctionStatus(final int id) {
        this.id = id;
    }

    public static AuctionStatus ofInt(final int id) throws IllegalArgumentException {
        switch (id) {
            case 1 -> {
                return AuctionStatus.OPEN;
            }
            case 2 -> {
                return AuctionStatus.CLOSED;
            }
            default -> throw new IllegalArgumentException(id + " is not a valid AuctionStatus id.");
        }
    }
}
