package models;

import java.awt.Checkbox;
import java.util.List;
import java.util.function.*;

public record Genre(int id, String name) {
    public static Supplier<String> anyGenre = () -> "Any";
    private final static Function<List<Checkbox>, List<String>> filterSelectedGenres = (checkboxes) ->
            checkboxes.stream().filter(Checkbox::getState).map(Checkbox::getLabel).toList();

    private final static BiPredicate<String, Genre> matchesLabel = (label, genre) -> label.equals(genre.name());
    public static BiFunction<List<Checkbox>, List<Genre>, List<Genre>> getSelectedGenres =
            (checkboxes, genres) ->
                    genres.stream()
                            .filter(genre -> filterSelectedGenres.apply(checkboxes).stream()
                                    .anyMatch(label -> matchesLabel.test(label, genre)))
                            .toList();
    public static Function<List<Checkbox>, Boolean> hasSelectedGenre = (checkboxes) ->
            checkboxes.stream()
                    .anyMatch(Checkbox::getState);

    public static Function<List<Genre>, List<Checkbox>> createGenreCheckboxes = (genres) ->
            genres.stream()
                    .map(genre -> new Checkbox(genre.name()))
                    .toList();
}

