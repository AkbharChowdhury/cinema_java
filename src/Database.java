import models.Genre;
import models.Messages;
import models.Movie;
import org.apache.commons.lang3.text.WordUtils;

import java.sql.*;
import java.text.MessageFormat;
import java.util.*;


import static models.Messages.printErrorMessage;

public class Database {
    private Database() {
    }

    private static volatile Database instance;

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

    public static Database getInstance() {
        try {
            if (instance == null) {
                synchronized (Database.class) {
                    if (instance == null) {
                        instance = new Database();
                    }
                }
            }
        } catch (Exception ex) {

            Messages.printErrorMessage.accept(ex.getMessage());

        }

        return instance;


    }

    public List<String> getMovieGenres() {
        List<String> list = new ArrayList<>();
        try (var con = getConnection();
             var stmt = con.prepareStatement("SELECT DISTINCT genre FROM movie_genres NATURAL JOIN genres");
             var rs = stmt.executeQuery()) {

            while (rs.next()){
                list.add(rs.getString("genre"));
            }

        } catch (SQLException ex) {
            printErrorMessage.accept(ex.getMessage());

        }

        return list.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList().stream().map(WordUtils::capitalizeFully).toList();

    }

    public List<Movie> getMovieList() {
        List<Movie> list = new ArrayList<>();
        try (var con = getConnection();
             var stmt = con.createStatement();
             var rs = stmt.executeQuery("SELECT movie_id, title, genres  FROM view_all_movies")
        ) {

            while (rs.next()) {
                int id = rs.getInt("movie_id");
                String title = rs.getString("title");
                String genres = rs.getString("genres");
                list.add(new Movie(id, title, genres));

            }

        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());

        }
        return list;

    }


    public List<Genre> getAllGenres() {
        List<Genre> list = new ArrayList<>();
        try (var con = getConnection();
             var stmt = con.createStatement();
             var rs = stmt.executeQuery("SELECT genre, genre_id FROM genres")
        ) {

            while (rs.next()) {
                list.add(new Genre(rs.getInt("genre_id"), rs.getString("genre")));
            }

        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());

        }
        return list;

    }

    public String getMovieName(int movieID) {

        try (var con = getConnection(); var stmt = con.prepareStatement("SELECT title FROM movies WHERE movie_id = ?")) {
            stmt.setInt(1, movieID);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) return rs.getString("title");
            }
        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
        }

        return MessageFormat.format("Error fetching movie name by movie id, Movie ID {0} does not exist", movieID);

    }


    public List<String> getSelectedMovieGenres(int movieID) {
        List<String> list = new ArrayList<>();
        try (var con = getConnection();
             var stmt = con.prepareStatement("SELECT genre FROM fn_get_selected_movie_genres(?)")) {
            stmt.setInt(1, movieID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("genre"));
            }
        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
        }
        return list.stream().toList();

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

    public void addMovieGenres(int movieID, List<Integer> genreIDs) {
        try (var con = getConnection()) {
            for (int genreId : genreIDs) {
                var stmt = con.prepareStatement("INSERT INTO movie_genres(genre_id, movie_id) VALUES(?, ?)");
                stmt.setInt(1, genreId);
                stmt.setInt(2, movieID);
                stmt.executeUpdate();
            }

        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
        }
    }

    public void updateMovieTitle(String title, int movieID) {
        try (var con = getConnection()) {
            var stmt = con.prepareStatement("UPDATE movies SET title = ? WHERE movie_id = ?");
            stmt.setString(1, title);
            stmt.setInt(2, movieID);
            stmt.executeUpdate();

        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
        }
    }

    public boolean addMovieAndGenres(String title, Set<Integer> genres) {
        try (var con = getConnection()) {
            var stmt = con.prepareStatement("CALL pr_add_movie_and_genres(?, ?)");
            Array genreArray = con.createArrayOf("INTEGER", new Object[]{genres.toArray(new Integer[0])});
            stmt.setString(1, title);
            stmt.setArray(2, genreArray);
            return stmt.executeUpdate() == -1;

        } catch (Exception ex) {
            printErrorMessage.accept(ex.getMessage());
        }
        return false;
    }
}



