package models;

import java.awt.Checkbox;
import java.util.List;

public final class GenreCheckboxFactory {
    private GenreCheckboxFactory() {}

    /** Creates a list of checkboxes for all genres */
    public static List<Checkbox> create(List<Genre> genres) {
        return genres.stream()
                .map(genre -> new Checkbox(genre.name()))
                .toList();
    }
}