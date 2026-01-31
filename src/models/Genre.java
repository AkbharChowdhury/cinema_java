package models;

import java.awt.Checkbox;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public record Genre(int id, String name) {
    private static final Function<List<Checkbox>, List<String>> filterSelectedGenres = (checkboxes) -> checkboxes.stream().filter(Checkbox::getState).map(Checkbox::getLabel).toList();

    public static BiFunction<List<Checkbox>, List<Genre>, List<Genre>> getSelectedGenres = (checkboxes, genres) ->
            genres.stream().filter(genre -> filterSelectedGenres.apply(checkboxes).stream()
                            .anyMatch(label -> label.equals(genre.name())))
                    .toList();
    public static Function<List<Checkbox>, Boolean> hasSelectedGenre = (checkboxes) -> checkboxes.stream().anyMatch(Checkbox::getState);

    public static Function<List<Genre>, List<Checkbox>> createGenreCheckboxes = (genres) ->
            genres.stream()
                    .map(genre -> new Checkbox(genre.name()))
                    .toList();
}

