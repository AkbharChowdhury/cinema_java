package models;

import java.awt.Checkbox;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public final class GenreUtils {
    private GenreUtils(){}
    public static final String ANY_GENRE = "Any";

    /** Extracts the labels of all selected checkboxes. */
    private static final Function<List<Checkbox>, Set<String>> filterSelectedGenres = checkboxes ->
            checkboxes.stream()
                    .filter(Checkbox::getState)
                    .map(Checkbox::getLabel)
                    .collect(HashSet::new, Set::add, Set::addAll);
    /** Checks if a genre's name matches a label. */
    private final static BiPredicate<String, Genre> matchesLabel = (label, genre) -> label.equals(genre.name());
    /**
     * Returns a list of genres corresponding to the selected checkboxes.
     *
     * @param checkboxes list of genre checkboxes
     * @param genres     list of all genres
     * @return list of genres selected in the UI
     */
    public static final BiFunction<List<Checkbox>, List<Genre>, List<Genre>> getSelectedGenres =
            (checkboxes, genres) -> {
                Set<String> selectedLabels = filterSelectedGenres.apply(checkboxes);
                return genres.stream()
                        .filter(genre -> selectedLabels.contains(genre.name()))
                        .toList();
            };

    public static Function<List<Checkbox>, Boolean> hasSelectedGenre = (checkboxes) ->
            checkboxes.stream()
                    .anyMatch(Checkbox::getState);

    public static Function<List<Genre>, List<Checkbox>> createGenreCheckboxes = (genres) ->
            genres.stream()
                    .map(genre -> new Checkbox(genre.name()))
                    .toList();
}
