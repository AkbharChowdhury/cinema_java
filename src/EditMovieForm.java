
import models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class EditMovieForm extends JFrame implements ActionListener {
    private static MainMenu mainMenu;
    private final int MOVIE_ID = MovieInfo.getMovieID();
    private final MovieDatabase db = MovieDatabase.getInstance();
    private final List<String> originalSelectedGenres = db.fetchMovieGenres(MOVIE_ID);

    final String MOVIE_TITLE = db.fetchMovieTitle(MOVIE_ID);
    private final List<Genre> genres = Collections.unmodifiableList(db.fetchAllGenres());
    private final JTextField txtTitle = new JTextField(40);
    private final JButton btnUpdateMovie = new JButton("Update Movie");
    private final JButton btnUndoTitle = new JButton("Undo title");
    private final JButton btnUndoGenre = new JButton("Undo Genre");
    private final JButton[] buttons = {btnUpdateMovie, btnUndoTitle, btnUndoGenre};
    private final List<Checkbox> genreCheckboxes;

    public EditMovieForm(MainMenu mainMenuForm) {
        mainMenu = mainMenuForm;
        txtTitle.setText(MOVIE_TITLE);
        setTitle("Edit Movie");
        JPanel panel = new JPanel();
        JPanel top = new JPanel();
        JPanel middle = new JPanel();
        panel.setLayout(new BorderLayout());

        top.add(new JLabel("Movie"));
        top.add(txtTitle);
        top.add(btnUndoTitle);
        top.add(btnUndoGenre);

        middle.setLayout(new GridLayout(genres.size(), 2));

        genreCheckboxes = Genre.createGenreCheckboxes.apply(genres);
        genreCheckboxes.forEach(middle::add);

        panel.add(top, BorderLayout.NORTH);
        panel.add(middle, BorderLayout.CENTER);
        panel.add(btnUpdateMovie, BorderLayout.SOUTH);
        setContentPane(panel);
        setDefaultCloseOperation(MyWindow.getCloseOperation());
        setSize(800, 400);

        Arrays.stream(buttons).forEach(button -> button.addActionListener(this));
        MyButton.applyHandCursor.accept(buttons);
        showOriginalSelectedGenres.accept(genreCheckboxes);

        setVisible(true);
    }


    private void undoGenreSelection() {
        resetGenreSelection.accept(genreCheckboxes);
        showOriginalSelectedGenres.accept(genreCheckboxes);

    }

    private final Consumer<List<Checkbox>> resetGenreSelection = (genreCheckboxes) -> genreCheckboxes.forEach(checkbox -> checkbox.setState(false));
    private final Consumer<List<Checkbox>> showOriginalSelectedGenres = (genreCheckboxes) ->
            genreCheckboxes.stream()
                    .filter(checkbox -> originalSelectedGenres.stream()
                            .anyMatch(label -> label.equals(checkbox.getLabel())))
                    .forEach(checkbox -> checkbox.setState(true));

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnUndoGenre) undoGenreSelection();

        if (e.getSource() == btnUpdateMovie) {
            if (!isFormValid()) return;
            updateGenres();
        }

        if (e.getSource() == btnUndoTitle) {
            txtTitle.setText("");
            txtTitle.setText(MOVIE_TITLE);
        }
    }

    private boolean isFormValid() {

        boolean hasSelectedGenre = Genre.hasSelectedGenre.apply(genreCheckboxes);
        if (txtTitle.getText().trim().isBlank()) {
            Messages.showErrorMessage("Title required!", "Movie title is required");
            return false;
        }
        if (!hasSelectedGenre) {
            Messages.showErrorMessage("Genre required!", "Please choose a genre");
            return false;
        }
        return true;
    }

    private void updateGenres() {
        db.updateMovieTitle(txtTitle.getText().trim(), MOVIE_ID);
        db.deleteRecord("movie_genres", TableName.MOVIE_ID, MOVIE_ID);
        List<Integer> selectedGenreIds = Genre.getSelectedGenres.apply(genreCheckboxes, genres).stream().map(Genre::id).toList();
        db.addGenresToMovie(MOVIE_ID, selectedGenreIds);
        Messages.message("Movie updated");
        redirectToMainMenu();
    }

    private void redirectToMainMenu() {
        if (mainMenu != null) mainMenu.dispose();
        dispose();
        new MainMenu();
    }

    void main() {
        new EditMovieForm(mainMenu);
    }
}

