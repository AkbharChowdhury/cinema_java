package models;

import java.awt.Checkbox;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class GenreSelectionUtils {
    private GenreSelectionUtils() {}

    public static final String ANY_GENRE = "Any";

    /** Returns a list of genres corresponding to selected checkboxes */
    public static List<Genre> getSelectedGenres(List<Checkbox> checkboxes, List<Genre> genres) {
        Set<String> selectedLabels = checkboxes.stream()
                .filter(Checkbox::getState)
                .map(Checkbox::getLabel)
                .collect(Collectors.toSet());

        return genres.stream()
                .filter(g -> selectedLabels.contains(g.name()))
                .toList();
    }

    /** Checks if any genre checkbox is selected */
    public static boolean hasSelectedGenre(List<Checkbox> checkboxes) {
        return checkboxes.stream().anyMatch(Checkbox::getState);
    }
}
