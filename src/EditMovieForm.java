import models.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


public class EditMovieForm extends JFrame {
    private static MainMenu mainMenu;
    private final int MOVIE_ID = MovieInfo.getMovieID();
    private final MovieDatabase db = MovieDatabase.getInstance();
    private final List<String> originalSelectedGenres = db.fetchMovieGenres(MOVIE_ID);
    private final String MOVIE_TITLE = db.fetchMovieTitle(MOVIE_ID);
    private final List<Genre> genres = Collections.unmodifiableList(db.fetchAllGenres());
    private final JTextField txtTitle = new JTextField(40);
    private final JButton btnUpdateMovie = ButtonFactory.createButton("Update Movie", _ -> updateMovieAction());
    private final JButton btnUndoGenre = ButtonFactory.createButton("Undo Genre", _ -> undoGenreSelection());
    private final JButton btnUndoTitle = ButtonFactory.createButton("Undo title", _ -> txtTitle.setText(MOVIE_TITLE));
    private final List<Checkbox> genreCheckboxes;

    public EditMovieForm(MainMenu mainMenuForm) {

        btnUpdateMovie.setToolTipText("Save changes to the movie");
        btnUndoGenre.setToolTipText("Undo changes to genres");
        btnUndoTitle.setToolTipText("Undo changes to the title");
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
        setDefaultCloseOperation(WindowUtils.getCloseOperation());
        setSize(800, 400);

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
                    .filter(checkbox -> originalSelectedGenres.stream().anyMatch(label -> label.equals(checkbox.getLabel())))
                    .forEach(checkbox -> checkbox.setState(true));


    private void updateMovieAction() {
        if (!MovieFormValidator.isFormValid(txtTitle, genreCheckboxes)) return;
        updateMovie();
    }


    private void updateMovie() {
        db.updateMovieTitle(txtTitle.getText().trim(), MOVIE_ID);
        db.deleteRecord("movie_genres", MovieSchema.MOVIE_ID, MOVIE_ID);
        List<Integer> selectedGenreIds = Genre.getSelectedGenres.apply(genreCheckboxes, genres).stream().map(Genre::id).toList();
        db.addGenresToMovie(MOVIE_ID, selectedGenreIds);
        Messages.message.accept("Movie updated");
//        redirectToMainMenu();
        WindowUtils.openMainMenu(this, mainMenu);
    }

//    private void redirectToMainMenu() {
//        if (mainMenu != null) mainMenu.dispose();
//        dispose();
//        new MainMenu();
//    }
    private void redirect(JFrame newForm, JFrame currentForm){
        if (newForm!=null) newForm.dispose();
        currentForm.dispose();





    }

    void main() {
        new EditMovieForm(mainMenu);
    }
}

