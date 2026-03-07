package models;

import java.awt.Checkbox;
import java.util.List;
import java.util.Objects;

public final class GenreCheckboxFactory {
    private GenreCheckboxFactory() {
    }

    public static List<Checkbox> create(List<Genre> genres) {
        Objects.requireNonNull(genres, "Genres must not be empty");
        return genres.stream()
                .map(genre -> new Checkbox(genre.name()))
                .toList();
    }
}