import models.Genre;
import models.Messages;
import models.Movie;
import org.apache.commons.lang3.text.WordUtils;

import java.sql.*;
import java.text.MessageFormat;
import java.util.*;


import static models.Messages.printErrorMessage;

public class MovieDatabase {
    private MovieDatabase() {
    }

    private static volatile MovieDatabase instance;

    private Connection getConnection() {
        Connection connection = null;
        try {
            Properties props = ENVManager.getENV();
            final String DB_NAME = "cinema";
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(String.format("jdbc:postgresql://localhost:5432/%s", DB_NAME), props.getProperty("USERNAME"), props.getProperty("PASSWORD"));
        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
        }
        return connection;

    }

    public static MovieDatabase getInstance() {
        try {
            if (instance == null) {
                synchronized (MovieDatabase.class) {
                    if (instance == null) {
                        instance = new MovieDatabase();
                    }
                }
            }
        } catch (Exception ex) {
            Messages.printErrorMessage.accept(ex.getMessage());
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
        List<Movie> movieList = new ArrayList<>();
        try (var con = getConnection();
             var stmt = con.createStatement();
             var rs = stmt.executeQuery("SELECT movie_id, title, genres  FROM view_all_movies")
        ) {
            while (rs.next()) {
                movieList.add(new Movie(rs.getInt(TableName.MOVIE_ID), rs.getString(TableName.MOVIE_TITLE), rs.getString(TableName.GENRES)));
            }

        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());

        }
        return movieList;

    }


    public List<Genre> fetchAllGenres() {
        List<Genre> genres = new ArrayList<>();
        try (var con = getConnection();
             var stmt = con.createStatement();
             var rs = stmt.executeQuery("SELECT genre, genre_id FROM genres")
        ) {

            while (rs.next()) {
                genres.add(new Genre(rs.getInt(TableName.GENRE_ID), rs.getString(TableName.GENRE)));
            }

        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());

        }
        return genres.stream().sorted(Comparator.comparing(Genre::name)).toList();

    }

    public String fetchMovieTitle(int movieId) {

        try (var con = getConnection();
             var stmt = con.prepareStatement("SELECT title FROM movies WHERE movie_id = ?")) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
        }

        return MessageFormat.format("Error fetching movie name by movie id, Movie ID {0} does not exist", movieId);

    }


    public List<String> fetchMovieGenres(int movieId) {
        List<String> genres = new ArrayList<>();
        try (var con = getConnection();
             var stmt = con.prepareStatement("SELECT genre FROM fn_get_selected_movie_genres(?);")) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                genres.add(rs.getString(1));
            }
        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
        }
        return genres;

    }


    public void deleteRecord(String tableName, String idField, int id) {
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(String.format("DELETE FROM %s WHERE %s = ?", tableName, idField))) {
            stmt.setInt(1, id);
            stmt.execute();
        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
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

        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
        }
    }

    public void updateMovieTitle(String newTitle, int movieId) {
        try (var con = getConnection()) {
            var stmt = con.prepareStatement("UPDATE movies SET title = ? WHERE movie_id = ?");
            stmt.setString(1, newTitle);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();

        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
        }
    }

    public boolean addMovieWithGenres(String title, Set<Integer> genreIds) {
        try (var con = getConnection()) {
            var stmt = con.prepareStatement("CALL pr_add_movie_and_genres(?, ?)");
            Array genreArray = con.createArrayOf("INTEGER", new Object[]{genreIds.toArray(new Integer[0])});
            stmt.setString(1, title);
            stmt.setArray(2, genreArray);
            return stmt.executeUpdate() == -1;

        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
        }
        return false;
    }
}



