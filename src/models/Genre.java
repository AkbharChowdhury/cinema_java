package models;

import java.awt.Checkbox;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public record Genre(int id, String name) {
    private static final Function<List<Checkbox>, List<String>> filterSelectedGenres = (checkboxes) ->
            checkboxes.stream().filter(Checkbox::getState).map(Checkbox::getLabel).toList();

    private static final BiPredicate<String, Genre> filterLabel = (label, genre) -> label.equals(genre.name());
    public static BiFunction<List<Checkbox>, List<Genre>, List<Genre>> getSelectedGenres =
            (checkboxes, genres) ->
                    genres.stream()
                            .filter(genre -> filterSelectedGenres.apply(checkboxes).stream()
                                    .anyMatch(label -> filterLabel.test(label, genre)))
                            .toList();
    public static Function<List<Checkbox>, Boolean> hasSelectedGenre = (checkboxes) ->
            checkboxes.stream()
                    .anyMatch(Checkbox::getState);

    public static Function<List<Genre>, List<Checkbox>> createGenreCheckboxes = (genres) ->
            genres.stream()
                    .map(genre -> new Checkbox(genre.name()))
                    .toList();
}

