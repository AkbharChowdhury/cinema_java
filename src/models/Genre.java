package models;

import java.awt.Checkbox;
import java.util.List;
import java.util.function.Function;

public record Genre(int id, String name) {
    public static List<Genre> getSelectedGenres(List<Checkbox> checkboxes, List<Genre> genres) {

        List<String> selectedGenres = checkboxes.stream().filter(Checkbox::getState).map(Checkbox::getLabel).toList();
        return genres.stream()
                .filter(genre -> selectedGenres.stream()
                        .anyMatch(label -> label.equals(genre.name())))
                .toList();
    }

    public static Function<List<Checkbox>, Boolean> hasSelectedGenre = (checkboxes) -> checkboxes.stream().anyMatch(Checkbox::getState);
}

