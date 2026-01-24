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
    private final Predicate<Movie> filterTitle = p -> StringUtils.containsIgnoreCase(p.title(), title);

    private Predicate<Movie> filterGenre() {
        return !"Any".equals(genre) ? p -> StringUtils.containsIgnoreCase(p.genres(), genre) : p -> true;
    }

    public SearchMovies(List<Movie> list) {
        this.list = list;
    }


    public Supplier<List<Movie>> filterResults = () ->
            list.stream()
                    .filter(filterTitle)
                    .filter(filterGenre())
                    .toList();


    @Override
    public String toString() {
        return list.stream().toList().toString();
    }
}