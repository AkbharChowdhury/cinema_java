package models;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class SearchMovies {

    @Setter
    @Getter
    private List<Movie> list;

    @Setter
    public String title = "";
    @Setter
    private String genre = Genre.anyGenre.get();
    private final Predicate<Movie> filterByTitle = movie -> StringUtils.containsIgnoreCase(movie.title(), title);
    private final Predicate<Movie> filterByGenre = movie -> Genre.anyGenre.get().equals(genre) || StringUtils.containsIgnoreCase(movie.genres(), genre);

    public SearchMovies(List<Movie> list) {
        this.list = list;
    }

    public Supplier<List<Movie>> filterMovies = () -> list.stream().filter(filterByTitle.and(filterByGenre)).toList();

}