import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import models.Genre;
import models.Movie;
import org.apache.commons.lang3.text.WordUtils;

import static models.Messages.printErrorMessage;


public final class MovieDatabase {

    private static final Set<String> ALLOWED_TABLES = Set.of("movies", "genres", "movie_genres");
    private static final Set<String> ALLOWED_ID_FIELDS = Set.of("movie_id", "genre_id");
    private static final String UPDATE_MOVIE_TITLE_SQL = "UPDATE movies SET title = ? WHERE movie_id = ?";
    private static final String ADD_MOVIE_WITH_GENRES_SQL = "CALL pr_add_movie_and_genres(?, ?)";
    //language=SQL
    private static final String FETCH_AVAILABLE_GENRES_SQL = "SELECT genre FROM available_genres ORDER BY genre";
    //language=SQL
    private static final String FETCH_MOVIE_GENRES_SQL = "SELECT genre FROM fn_get_selected_movie_genres(?) ORDER BY genre";
    //language=SQL
    private static final String FETCH_MOVIE_TITLE_SQL = "SELECT title FROM movies WHERE movie_id = ?";
    //language=SQL
    private static final String FETCH_ALL_GENRES_SQL = "SELECT genre_id, genre FROM genres ORDER BY genre";
    //language=SQL
    private static final String FETCH_MOVIES_SQL = "SELECT movie_id, title, genres FROM view_all_movies";

    private HikariDataSource dataSource;
    private QueryBuilder queryBuilder;

    private static class Holder {
        private static final MovieDatabase INSTANCE = new MovieDatabase();
    }

    public static MovieDatabase getInstance() {
        return MovieDatabase.Holder.INSTANCE;
    }

    private MovieDatabase() {
        try {
            Properties props = EnvLoader.loadProperties();
            HikariConfig config = getConfig(props);
            dataSource = new HikariDataSource(config);
            queryBuilder = new QueryBuilder(dataSource);
            // Shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(this::closeConnection));
        } catch (Exception ex) {
            System.err.println("There was an error establishing a connection " + ex.getMessage());
        }


    }

    private void closeConnection() {
        // Close the pool
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private HikariConfig getConfig(Properties props) {
        String template = props.getProperty("JDBC_URL_TEMPLATE");
        String dbName = props.getProperty("DB_NAME");
        String url = String.format(template, dbName);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setDriverClassName("org.postgresql.Driver");
        config.setUsername(props.getProperty("USERNAME"));
        config.setPassword(props.getProperty("PASSWORD"));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(300_000);
        config.setLeakDetectionThreshold(300_000);
        return config;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection(); // pooled connection
    }

    public List<String> fetchAvailableGenres() {
        return queryBuilder.query(FETCH_AVAILABLE_GENRES_SQL, rs -> WordUtils.capitalizeFully(rs.getString(1)));
    }


    public List<Movie> fetchMovies() {
        return queryBuilder.query(FETCH_MOVIES_SQL, rs -> new Movie(
                rs.getInt("movie_id"),
                rs.getString("title"),
                rs.getString("genres")
        ));
    }

    public List<Genre> fetchAllGenres() {
        return queryBuilder.query(FETCH_ALL_GENRES_SQL, rs -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre")
        ));
    }


    public Optional<String> fetchMovieTitle(int movieId) {
        return queryBuilder.query(FETCH_MOVIE_TITLE_SQL, rs -> rs.getString("title"), movieId)
                .stream()
                .findFirst();
    }


    public List<String> fetchMovieGenres(int movieId) {
        return queryBuilder.query(FETCH_MOVIE_GENRES_SQL, rs -> rs.getString("genre"), movieId);
    }


    public boolean deleteRecord(String tableName, String idField, int id) {
        List<String> errors = new ArrayList<>();

        if (!ALLOWED_TABLES.contains(tableName)) {
            errors.add("Invalid table name: " + tableName + " Must be of " + ALLOWED_TABLES);
//            throw new IllegalArgumentException("Invalid table name: " + tableName + " Must be of " + ALLOWED_TABLES);
        }

        if (!ALLOWED_ID_FIELDS.contains(idField)) {
            String message = MessageFormat.format("""
                            Invalid id field: "{0}". Must be one of {1}.
                            """.trim(),
                    idField,
                    ALLOWED_ID_FIELDS
            );
            errors.add(message);

//            throw new IllegalArgumentException(message);
        }
        if(!errors.isEmpty()){
            throw new IllegalArgumentException("Sorry unable to delete record, Please see error messages: " + errors);
        }
        //language=SQL
        String sql = String.format("DELETE FROM %s WHERE %s = ?", tableName, idField);
        try (var con = getConnection();
             var stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() != 0;

        } catch (SQLException ex) {
            throw new RuntimeException(
                    "Failed to delete record from " + tableName +
                            " with id: " + id, ex);
        }
    }


    public void addGenresToMovie(int movieId, List<Integer> genreIds) {
        try (Connection con = getConnection()) {
            for (int genreID : genreIds) {
                var stmt = con.prepareStatement("INSERT INTO movie_genres(genre_id, movie_id) VALUES(?, ?)");
                stmt.setInt(1, genreID);
                stmt.setInt(2, movieId);
                stmt.executeUpdate();
            }

        } catch (SQLException ex) {
            printErrorMessage.accept(ex.getMessage());
        }
    }


    public void updateMovieTitle(String newTitle, int movieId) {

        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be empty");
        }

        try (var con = getConnection();
             var stmt = con.prepareStatement(UPDATE_MOVIE_TITLE_SQL)) {

            stmt.setString(1, newTitle);
            stmt.setInt(2, movieId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new IllegalArgumentException(
                        MessageFormat.format("This movie id {0} does not exist", movieId));
            }

        } catch (SQLException ex) {
            throw new RuntimeException(
                    "Failed to update movie title for movie_id: " + movieId, ex);
        }
    }

    public boolean addMovieWithGenres(String title, Set<Integer> genreIds) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be empty");
        }

        if (genreIds == null || genreIds.isEmpty()) {
            throw new IllegalArgumentException("At least one genre must be provided");
        }

        try (var con = getConnection();
             var stmt = con.prepareCall(ADD_MOVIE_WITH_GENRES_SQL)) {
            stmt.setString(1, title);
            Array genreArray = con.createArrayOf("INTEGER", genreIds.toArray(new Integer[0]));
            stmt.setArray(2, genreArray);
            stmt.execute();  // use execute() for procedures
            return true;

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to add movie with genres. Title: " + title, ex);
        }
    }


}



