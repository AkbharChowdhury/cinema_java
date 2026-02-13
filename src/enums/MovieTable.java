package enums;

public enum MovieTable {
    MOVIES("movies"),
    MOVIE_GENRES("movie_genres");

    private final String tableName;

    MovieTable(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return tableName;
    }
}