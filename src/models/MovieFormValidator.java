package models;

import java.awt.Checkbox;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JTextField;

public final class MovieFormValidator {
    private MovieFormValidator() {

    }

    public static List<String> addMovieFormErrors(String title, Set<Integer> genreIds) {
        ArrayList<String> errors = new ArrayList<>();
        if (title == null || title.isBlank()) {
            errors.add("Title cannot be empty");
        }

        if (genreIds == null || genreIds.isEmpty()) {
            errors.add("At least one genre must be provided");
        }
        return errors;
    }


    public static boolean isFormValid(JTextField txtTitle, List<Checkbox> genreCheckboxes) {
        boolean hasSelectedGenre = GenreSelectionUtils.hasSelectedGenre(genreCheckboxes);
        String title = txtTitle.getText();
        if (title == null || title.isBlank()) {
            Messages.showError.accept("Title is Empty", "Please enter a movie title");
            return false;
        }
        if (!hasSelectedGenre) {
            Messages.showError.accept("Missing Genre", "Please select at least one genre");
            return false;
        }
        return true;

    }
}
