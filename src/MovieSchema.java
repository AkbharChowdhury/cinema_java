import enums.MovieTable;

public final class MovieSchema {
    private MovieSchema() {
    }

    public static boolean deleteMovie(MovieTable table, int movieId, MovieDatabase db) {
        return db.deleteRecord(table.getName(), "movie_id", movieId);
    }

}
