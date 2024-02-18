package works.quiet.db;

import lombok.Getter;

import java.util.Arrays;

@Getter
public final class UpsertHelper {
    private final String fieldNames;
    private final String placeholders;
    private final String updateDescription;
    private final Object[] values;

    public UpsertHelper(final boolean omitId, final String[] fields, final Object[] values) {
        var processedFields = processElements(omitId, fields);
        this.fieldNames = join(processedFields);
        this.placeholders = join(processPlaceholders(processedFields));
        this.updateDescription = join(processUpdateDescription(processElements(true, fields)));
        this.values = processElements(omitId, values);
    }

    private <T> T[] processElements(final boolean omitId, final T[] elements) {
        try {
            return omitId ? Arrays.copyOfRange(elements, 1, elements.length) : elements;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String[] processPlaceholders(final String[] fields) {
        return Arrays.stream(fields).map((x) -> "?").toArray(String[]::new);
    }

    private String[] processUpdateDescription(final String[] fields) {
        return Arrays.stream(fields).map(field -> field + "=excluded." + field).toArray(String[]::new);
    }

    private String join(final String[] values) {
        return String.join(",", values);
    }
}
