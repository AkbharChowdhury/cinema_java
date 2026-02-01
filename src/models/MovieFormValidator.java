package models;

import javax.swing.JTextField;
import java.awt.Checkbox;

import java.util.List;

public class MovieFormValidator {
    private MovieFormValidator() {

    }

    public static boolean isMovieFormValid(JTextField txtTitle, List<Checkbox> genreCheckboxes) {
        boolean hasSelectedGenre = Genre.hasSelectedGenre.apply(genreCheckboxes);
        if (txtTitle.getText().trim().isBlank()) {
            Messages.showErrorMessage.accept("Title is Empty", "Please enter a movie title");
            return false;
        }
        if (!hasSelectedGenre) {
            Messages.showErrorMessage.accept("Missing Genre", "Please select at least one genre");
            return false;
        }
        return true;
    }
}
