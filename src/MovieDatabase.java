import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import interfaces.SQLConsumer;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import models.Genre;
import models.Movie;
import models.MovieFormValidator;
import models.RecordValidator;
import org.apache.commons.lang3.text.WordUtils;

import static models.Messages.printErrorMessage;

public final class MovieDatabase {

    // language=SQL
    private static final String UPDATE_MOVIE_TITLE_SQL = "UPDATE movies SET title = ? WHERE movie_id = ?";
    //language=SQL
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
    //language=SQL
    private static final String INSERT_MOVIE_GENRES_SQL = "INSERT INTO movie_genres(genre_id, movie_id) VALUES(?, ?)";
    //language=SQL
    private static final String DELETE_SQL = "DELETE FROM %s WHERE %s = ?";

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
    private int executeUpdate(String sql, SQLConsumer<PreparedStatement> parameterSetter) {
        try (var con = getConnection();
             var stmt = con.prepareStatement(sql)) {
            parameterSetter.accept(stmt);
            return stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Database update failed: ", ex);
        }
    }

    public List<String> fetchAvailableGenres() {
        return queryBuilder.query(FETCH_AVAILABLE_GENRES_SQL, rs -> WordUtils.capitalizeFully(rs.getString(1)));
    }


    public List<Movie> fetchMovies() {
        return queryBuilder.query(FETCH_MOVIES_SQL, rs -> new Movie(rs.getInt("movie_id"), rs.getString("title"), rs.getString("genres")));
    }

    public List<Genre> fetchAllGenres() {
        return queryBuilder.query(FETCH_ALL_GENRES_SQL, rs -> new Genre(rs.getInt("genre_id"), rs.getString("genre")));
    }


    public Optional<String> fetchMovieTitle(int movieId) {
        return queryBuilder.query(FETCH_MOVIE_TITLE_SQL, rs -> rs.getString("title"), movieId).stream().findFirst();
    }

    public List<String> fetchMovieGenres(int movieId) {
        return queryBuilder.query(FETCH_MOVIE_GENRES_SQL, rs -> rs.getString("genre"), movieId);
    }


    public boolean deleteRecord(String tableName, String idField, int id) {
        RecordValidator.validateDelete(tableName, idField);
        String sql = String.format(DELETE_SQL, tableName, idField);
        return executeUpdate(sql, stmt -> stmt.setInt(1, id)) != 0;
    }



    public void addGenresToMovie(int movieId, List<Integer> genreIds) {
        try (var con = getConnection();
             var stmt = con.prepareStatement(INSERT_MOVIE_GENRES_SQL)) {
            for (int genreID : genreIds) {
                stmt.setInt(1, genreID);
                stmt.setInt(2, movieId);
                stmt.addBatch();   // Add to batch instead of executing immediately
            }

            stmt.executeBatch(); // Execute all at once

        } catch (SQLException ex) {

            printErrorMessage.accept(ex.getMessage());
        }
    }


    public void updateMovieTitle(String newTitle, int movieId) {
        if (newTitle == null || newTitle.isBlank()) throw new IllegalArgumentException("Movie title cannot be empty");
        SQLConsumer<PreparedStatement> parameterSetter = stmt -> {
            stmt.setString(1, newTitle);
            stmt.setInt(2, movieId);
        };

        int affectedRows = executeUpdate(UPDATE_MOVIE_TITLE_SQL, parameterSetter);
        if (affectedRows == 0)
            throw new RuntimeException("Failed to update movie title This Movie ID does not exist " + movieId);

    }

    public boolean addMovieWithGenres(String title, Set<Integer> genreIds) {
        List<String> errors = MovieFormValidator.validate(title, genreIds);
        if (!errors.isEmpty())
            throw new IllegalArgumentException("Cannot add the movie because of the following: " + errors);

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



