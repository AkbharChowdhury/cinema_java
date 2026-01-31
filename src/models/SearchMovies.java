package models;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class SearchMovies {

    @Setter
    @Getter
    private List<Movie> list;
    @Setter
    private String title = "";
    @Setter
    private String genre = "Any";
    private final Predicate<Movie> filterTitle = movie -> StringUtils.containsIgnoreCase(movie.title(), title);
    private final Predicate<Movie> filterGenre = movie -> "Any".equals(genre) || StringUtils.containsIgnoreCase(movie.genres(), genre);

    public SearchMovies(List<Movie> list) {
        this.list = list;
    }

    public Supplier<List<Movie>> filterResults = () -> list.stream().filter(filterTitle.and(filterGenre)).toList();

    @Override
    public String toString() {
        return list.stream().toList().toString();
    }
}