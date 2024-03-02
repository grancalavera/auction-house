package works.quiet.cli.etc;

import org.junit.jupiter.api.Test;
import works.quiet.auction.Auction;
import works.quiet.auction.Bid;
import works.quiet.user.User;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScratchpadTest {
    public ScratchpadTest() {

        // Detail: A JDBC 4.2 compliant driver might use an Instant, but is required to use an OffsetDateTime. To
        // convert: OffsetDateTime odt = myInstant.atOffset( ZoneOffset.UTC ) ;

        var i = Instant.now();
        var o = i.atOffset(ZoneOffset.UTC);

        // https://stackoverflow.com/a/6627999
        Calendar tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        // https://www.baeldung.com/java-time-instant-to-java-sql-timestamp

        // If you want to record the physical instant at when a particular event ocurred, (a true "timestamp" ,
        // typically some creation/modification/deletion event), then use:
        //
        // Java: Instant (Java 8 , or Jodatime).
        //         JDBC: java.sql.Timestamp
        // PostgreSQL: TIMESTAMP WITH TIMEZONE (TIMESTAMPTZ)

        BigInteger x = BigInteger.valueOf(1);
        int y = x.intValueExact();
        var z = foo();
    }

    private Object[] foo(final Object... values) {
        return values;
    }

    @Test
    void test() {
        Instant instant = Instant.now();
        Timestamp timestamp = Timestamp.from(instant);
        assertEquals(instant.toEpochMilli(), timestamp.getTime());

        var a = instant.toString();
        var b = timestamp.toString();

        instant = timestamp.toInstant();
        assertEquals(instant.toEpochMilli(), timestamp.getTime());
    }

    @Test
    void nullUserId() {
        assertEquals(0, User.builder().build().getId());
    }

    @Test
    void skipFirst() {
        String[] actual = {"a", "b"};
        actual = Arrays.copyOfRange(actual, 1, actual.length);
        String[] expected = {"b"};
        assertEquals(Arrays.toString(expected), Arrays.toString(actual));
    }

    @Test
    void arrayEquality() {
        String[] a = {"a"};
        String[] b = {"a"};
        assertArrayEquals(a, b);
    }

    @Test
    void joinArrayOfString() {
        String[] source = {"foo", "bar"};
        String actual = String.join(",", source);
        assertEquals("foo,bar", actual);
    }

    @Test
    void roundingModes() {
        var a = BigDecimal.valueOf(1);
        var b = BigDecimal.valueOf(4);
        var c = a.divide(b, RoundingMode.DOWN);
        var c1 = c.intValue();

        var d = a.divide(b, RoundingMode.FLOOR);
        var d2 = d.intValue();

        assertEquals(c, d);
    }

    void bar() {
        var auction = Auction.builder()
                .sellerId(1)
                .price(BigDecimal.valueOf(1))
                .quantity(2)
                .symbol("a")
                .createdAt(Instant.ofEpochSecond(1))
                .closedAt(Instant.ofEpochSecond(3))
                .build();

        var bid = Bid.builder()
                .auctionId(auction.getId())
                .amount(BigDecimal.valueOf(2))
                .createdAt(Instant.ofEpochSecond(1))
                .build();

        auction.getBids().add(bid);
    }
}
