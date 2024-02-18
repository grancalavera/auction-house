package works.quiet.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UpsertHelperTest {
    @Test
    @DisplayName("Exclude first field, then generate names, placeholders and update description.")
    void excludeFirstField() {
        String[] fields = {"a", "b", "c"};
        Object[] values = {1, 2, 3};
        var helper = new UpsertHelper(true, fields, values);
        assertEquals("b,c", helper.getFieldNames());
        assertEquals("?,?", helper.getPlaceholders());
        assertEquals("b=excluded.b,c=excluded.c", helper.getUpdateDescription());
        assertArrayEquals(new Object[]{2, 3}, helper.getValues());

    }

    @Test
    @DisplayName("Include first field, then generate names, placeholders and update description.")
    void includeFirstField() {
        String[] fields = {"a", "b", "c"};
        Object[] values = {1, 2, 3};
        var helper = new UpsertHelper(false, fields, values);
        assertEquals("a,b,c", helper.getFieldNames());
        assertEquals("?,?,?", helper.getPlaceholders());
        assertEquals("b=excluded.b,c=excluded.c", helper.getUpdateDescription());
        assertArrayEquals(new Object[]{1, 2, 3}, helper.getValues());
    }
}
