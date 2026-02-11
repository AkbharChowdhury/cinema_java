import models.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class AddMovieForm extends JFrame {
    private static MainMenu mainMenu;
    private final MovieDatabase db = MovieDatabase.getInstance();
    private final List<Genre> genres = Collections.unmodifiableList(db.fetchAllGenres());
    private final JTextField txtTitle = new JTextField(20);
    private final JButton btnAddMovie = ButtonFactory.createButton("Add Movie", _ -> handleAddMovie());
    private final List<Checkbox> genreCheckboxes;

    public AddMovieForm(MainMenu mainMenuForm) {
        mainMenu = mainMenuForm;

        btnAddMovie.setToolTipText("Add a new movie with the selected genres");
        setTitle("Add Movie");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel top = new JPanel();
        JPanel middle = new JPanel();
        top.add(new JLabel("Movie"));
        top.add(txtTitle);
        middle.setLayout(new GridLayout(genres.size(), 2));

        genreCheckboxes = Genre.createGenreCheckboxes.apply(genres);
        genreCheckboxes.forEach(middle::add);

        panel.add(top, BorderLayout.NORTH);
        panel.add(middle, BorderLayout.CENTER);
        panel.add(btnAddMovie, BorderLayout.SOUTH);

        setContentPane(panel);
        setDefaultCloseOperation(WindowUtils.getCloseOperation());

        setSize(400, 400);
        WindowUtils.applyAutofocus.accept(txtTitle);

    }


    private void handleAddMovie() {
        if (!MovieFormValidator.isFormValid(txtTitle, genreCheckboxes)) return;
        List<Integer> selectedGenres = Genre.getSelectedGenres.apply(genreCheckboxes, genres).stream().map(Genre::id).toList();
        boolean hasAddedMovie = db.addMovieWithGenres(txtTitle.getText().trim(), new HashSet<>(selectedGenres));
        if (!hasAddedMovie) {
            Messages.showErrorMessage.accept("", "There was an error adding the movie");
            return;
        }

        clearForm();
        Messages.message.accept("Movie Added");
//        redirectToMainMenu();
        WindowUtils.openMainMenu(this, mainMenu);
    }
//
//    private void redirectToMainMenu() {
//        if (mainMenu != null) mainMenu.dispose();
//        dispose();
//        new MainMenu();
//    }



    private void clearForm() {
        txtTitle.setText("");
        genreCheckboxes.forEach(checkbox -> checkbox.setState(false));
    }


    static void main() {
        new AddMovieForm(mainMenu).setVisible(true);
    }

}

