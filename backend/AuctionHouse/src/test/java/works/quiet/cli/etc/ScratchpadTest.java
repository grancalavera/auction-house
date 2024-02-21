package works.quiet.cli.etc;

import org.junit.jupiter.api.Test;
import works.quiet.user.User;

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


}
