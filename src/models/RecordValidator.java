package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class RecordValidator {
    private static final Set<String> ALLOWED_TABLES = Set.of("movies", "genres", "movie_genres");
    private static final Set<String> ALLOWED_ID_FIELDS = Set.of("movie_id", "genre_id");

    public static List<String> deleteRecordErrors(String tableName, String idField) {
        List<String> errors = new ArrayList<>();
        if (!ALLOWED_TABLES.contains(tableName))
            errors.add(String.format("Invalid table name: \"%s\". Must be one of %s", tableName, ALLOWED_TABLES));
        if (!ALLOWED_ID_FIELDS.contains(idField))
            errors.add(String.format("Invalid id field: \"%s\". Must be one of %s", idField, ALLOWED_ID_FIELDS));
        return errors;
    }
}
