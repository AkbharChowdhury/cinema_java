package models;

import javax.swing.JTextField;
import java.awt.Checkbox;

import java.util.List;

public final class MovieFormValidator {
    private MovieFormValidator() {

    }

    public static boolean isFormValid(JTextField txtTitle, List<Checkbox> genreCheckboxes) {
        boolean hasSelectedGenre = GenreSelectionUtils.hasSelectedGenre(genreCheckboxes);
        String title = txtTitle.getText();
        if (title == null || title.isBlank()) {
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
