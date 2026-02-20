package models;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public final class SearchMovies {

    @Setter
    @Getter
    private List<Movie> movies;
    @Setter
    private String title = "";
    @Setter
    private String genre = GenreSelectionUtils.ANY_GENRE;
    private final Predicate<Movie> filterByTitle = movie -> StringUtils.containsIgnoreCase(movie.title(), title);
    private final Predicate<Movie> filterByGenre = movie -> GenreSelectionUtils.ANY_GENRE.equals(genre) || containsGenre(movie, genre);

    public SearchMovies(List<Movie> movieList) {
        movies = movieList;
    }
    /**
     * Returns a filtered list of movies matching the current title and genre.
     */
    public List<Movie> filter() {
        return movies.stream()
                .filter(filterByTitle.and(filterByGenre))
                .toList();
    }

    /**
     * Returns true if the movie contains the selected genre (exact match, case-insensitive).
     */
    private static boolean containsGenre(Movie movie, String selectedGenre) {
        return Arrays.stream(movie.genres().split(Pattern.quote("|")))
                .map(String::trim)
                .anyMatch(g -> g.equalsIgnoreCase(selectedGenre));
    }


}