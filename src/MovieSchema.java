import java.util.Set;

public class MovieSchema {
    private MovieSchema() {
    }

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_TABLE = "movies";
    private static final Set<String> ALLOWED_TABLES = Set.of("movies", "movie_genres");

    public static boolean deleteMovie(String tableName, int movieId, MovieDatabase db) {
        if (!ALLOWED_TABLES.contains(tableName)) {
            System.err.println("table is not valid and must be of " + ALLOWED_TABLES);
            return false;
        }
        return db.deleteRecord(tableName, "movie_id", movieId);
    }

}
