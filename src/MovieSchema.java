import enums.MovieTable;

public final class MovieSchema {
    private MovieSchema() {
    }

    public static boolean deleteMovie(MovieTable table, int movieId, MovieDatabase db) {
        System.out.println("Table Name " + table.getName());
        return db.deleteRecord(table.getName(), "movie_id", movieId);
    }

}
