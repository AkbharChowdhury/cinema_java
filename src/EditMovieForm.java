
import enums.MovieTable;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;


import java.awt.Checkbox;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import models.ButtonFactory;
import models.Genre;
import models.Messages;
import models.MovieFormValidator;


public class EditMovieForm extends JFrame {
    private static MainMenu mainMenu;
    private final int MOVIE_ID;
    private final MovieDatabase db = MovieDatabase.getInstance();
    private  List<String> originalSelectedGenres;
    private final String MOVIE_TITLE;
    private final List<Genre> genres = Collections.unmodifiableList(db.fetchAllGenres());
    private final JTextField txtTitle = new JTextField(40);
    private final JButton btnUpdateMovie = ButtonFactory.createButton("Update Movie", _ -> updateMovieAction());
    private final JButton btnUndoGenre = ButtonFactory.createButton("Undo Genre", _ -> undoGenreSelection());
    private final JButton btnUndoTitle;
    private final List<Checkbox> genreCheckboxes;

    public EditMovieForm(MainMenu mainMenuForm, int movieId) {
        MOVIE_ID = movieId;
        MOVIE_TITLE = db.fetchMovieTitle(MOVIE_ID).orElse("Movie title not found");
        btnUndoTitle = ButtonFactory.createButton("Undo title", _ -> txtTitle.setText(MOVIE_TITLE));
        originalSelectedGenres =  db.fetchMovieGenres(MOVIE_ID);
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
        setDefaultCloseOperation(MainMenuState.getCloseOperation());
        setSize(800, 400);

        showOriginalSelectedGenres.accept(genreCheckboxes);

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
        MovieSchema.deleteMovie(MovieTable.MOVIE_GENRES, MOVIE_ID, db);
        List<Integer> selectedGenreIds = Genre.getSelectedGenres.apply(genreCheckboxes, genres).stream().map(Genre::id).toList();
        db.addGenresToMovie(MOVIE_ID, selectedGenreIds);
        Messages.message.accept("Movie updated");
        WindowUtils.openMainMenu(this, mainMenu);
    }

    void main() {
        new EditMovieForm(mainMenu, 0);
    }
}

