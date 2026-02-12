import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
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

public class MovieDatabase {
    private static final Set<String> ALLOWED_TABLES =  Set.of("movies", "genres", "movie_genres");
    private static final Set<String> ALLOWED_ID_FIELDS =  Set.of("movie_id", "genre_id");

    private static final String UPDATE_MOVIE_TITLE_SQL = "UPDATE movies SET title = ? WHERE movie_id = ?";
    private static final String ADD_MOVIE_WITH_GENRES_SQL = "CALL pr_add_movie_and_genres(?, ?)";

    private static final String FETCH_MOVIE_GENRES_SQL = "SELECT genre FROM fn_get_selected_movie_genres(?) ORDER BY genre";
    private static final String FETCH_MOVIE_TITLE_SQL = "SELECT title FROM movies WHERE movie_id = ?";
    private static final String FETCH_ALL_GENRES_SQL = "SELECT genre_id, genre FROM genres ORDER BY genre";
    private static final String FETCH_MOVIES_SQL = "SELECT movie_id, title, genres FROM view_all_movies";

    private MovieDatabase() {
    }

    private static volatile MovieDatabase instance;

    private Connection getConnection() {
        Connection connection = null;
        try {
            Properties props = EnvLoader.load();
            final String DB_NAME = "cinema";
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(String.format("jdbc:postgresql://localhost:5432/%s", DB_NAME), props.getProperty("USERNAME"), props.getProperty("PASSWORD"));
        } catch (ClassNotFoundException | SQLException ex) {
            printErrorMessage.accept(ex.getMessage());
        }
        return connection;

    }

    public static MovieDatabase getInstance() {
        if (instance == null) {
            synchronized (MovieDatabase.class) {
                if (instance == null) {
                    instance = new MovieDatabase();
                }
            }
        }

        return instance;

    }

    public List<String> fetchAvailableGenres() {
        List<String> availableGenres = new ArrayList<>();
        try (var con = getConnection();
             var stmt = con.prepareStatement("SELECT genre FROM available_genres");
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                availableGenres.add(rs.getString(1));
            }

        } catch (SQLException ex) {
            printErrorMessage.accept(ex.getMessage());
        }

        return availableGenres.stream().sorted(String.CASE_INSENSITIVE_ORDER).map(WordUtils::capitalizeFully).toList();

    }

    public List<Movie> fetchMovies() {
        List<Movie> movies = new ArrayList<>();

        try (var con = getConnection();
             var stmt = con.prepareStatement(FETCH_MOVIES_SQL);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("genres")
                ));
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch movies", ex);
        }

        return movies;
    }


    public List<Genre> fetchAllGenres() {
        try (var con = getConnection();
             var stmt = con.prepareStatement(FETCH_ALL_GENRES_SQL);
             var rs = stmt.executeQuery()) {

            var result = new ArrayList<Genre>();

            while (rs.next()) {
                result.add(new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("genre")
                ));
            }

            return List.copyOf(result);

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch genres", ex);
        }
    }


    public Optional<String> fetchMovieTitle(int movieId) {
        try (var con = getConnection();
             var stmt = con.prepareStatement(FETCH_MOVIE_TITLE_SQL)) {

            stmt.setInt(1, movieId);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("title"));
                }
            }

            return Optional.empty();

        } catch (SQLException ex) {
            throw new RuntimeException(
                    "Failed to fetch movie title for id: " + movieId, ex);
        }
    }


    public List<String> fetchMovieGenres(int movieId) {
        try (var con = getConnection();
             var stmt = con.prepareStatement(FETCH_MOVIE_GENRES_SQL)) {

            stmt.setInt(1, movieId);

            try (var rs = stmt.executeQuery()) {

                var genres = new ArrayList<String>();

                while (rs.next()) {
                    genres.add(rs.getString("genre"));
                }

                return List.copyOf(genres);
            }

        } catch (SQLException ex) {
            throw new RuntimeException(
                    "Failed to fetch genres for movie id: " + movieId, ex);
        }
    }


    public void deleteRecord(String tableName, String idField, int id) {

        if (!ALLOWED_TABLES.contains(tableName)) {
            throw new IllegalArgumentException("Invalid table name: " + tableName + " Must be of " + ALLOWED_TABLES);
        }

        if (!ALLOWED_ID_FIELDS.contains(idField)) {
            throw new IllegalArgumentException("Invalid id field: " + idField + " Must be of " + ALLOWED_ID_FIELDS);
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + idField + " = ?";

        try (var con = getConnection();
             var stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

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

            // PostgreSQL integer array
            Array genreArray = con.createArrayOf("INTEGER", genreIds.toArray(new Integer[0]));
            stmt.setArray(2, genreArray);

            stmt.execute();  // use execute() for procedures

            return true;

        } catch (SQLException ex) {
            throw new RuntimeException(
                    "Failed to add movie with genres. Title: " + title, ex);
        }
    }


}



