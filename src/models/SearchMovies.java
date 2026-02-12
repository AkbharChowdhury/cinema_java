package models;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
    private String genre = Genre.anyGenre.get();
    private final Predicate<Movie> filterByTitle = movie -> StringUtils.containsIgnoreCase(movie.title(), title);
    private final Predicate<Movie> filterByGenre = movie -> Genre.anyGenre.get().equals(genre) || StringUtils.containsIgnoreCase(movie.genres(), genre);

    public SearchMovies(List<Movie> movieList) {
        movies = movieList;
    }

    public List<Movie> filter() {
        return movies.stream()
                .filter(filterByTitle.and(filterByGenre))
                .collect(Collectors.toCollection(ArrayList::new));
//                .toList();
    }

}