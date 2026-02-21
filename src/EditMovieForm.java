import enums.MovieTable;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import models.ButtonFactory;
import models.Genre;
import models.GenreSelectionUtils;
import models.Messages;
import models.MovieFormValidator;


public class EditMovieForm extends JFrame {
    private final MainMenu mainMenu;
    private final int MOVIE_ID;
    private final MovieDatabase db = MovieDatabase.getInstance();
    private final List<String> originalSelectedGenres;
    private final String MOVIE_TITLE;
    private final List<Genre> genres = Collections.unmodifiableList(db.fetchAllGenres());
    private final JTextField txtTitle = new JTextField(40);
    private final JButton btnUpdateMovie = ButtonFactory.createButton("Update Movie", _ -> updateMovieAction());
    private final JButton btnUndoGenre = ButtonFactory.createButton("Undo Genre", _ -> undoGenreSelection());
    private final JButton btnUndoTitle;
    private final List<Checkbox> genreCheckboxes;
    private List<Checkbox> createGenreCheckboxes() {
        return genres.stream()
                .map(this::createCheckbox)
                .toList();
    }
    private Checkbox createCheckbox(Genre genre){
        Checkbox checkbox = new Checkbox(genre.name());
        checkbox.setState(originalSelectedGenres.contains(genre.name()));
        return checkbox;

    }

    public EditMovieForm(MainMenu mainMenuForm, int movieId) {
        MOVIE_ID = movieId;
        MOVIE_TITLE = db.fetchMovieTitle(MOVIE_ID).orElse("Movie title not found");
        btnUndoTitle = ButtonFactory.createButton("Undo title", _ -> txtTitle.setText(MOVIE_TITLE));
        originalSelectedGenres =  List.copyOf(db.fetchMovieGenres(MOVIE_ID));
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

        genreCheckboxes = createGenreCheckboxes();
        genreCheckboxes.forEach(middle::add);

        panel.add(top, BorderLayout.NORTH);
        panel.add(middle, BorderLayout.CENTER);
        panel.add(btnUpdateMovie, BorderLayout.SOUTH);
        setContentPane(panel);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(800, 400);

        showOriginalSelectedGenres();

    }


    private void undoGenreSelection() {
        resetGenreSelection();
        showOriginalSelectedGenres();
    }

    private void resetGenreSelection() {
        genreCheckboxes.forEach(cb -> cb.setState(false));
    }

    private void showOriginalSelectedGenres() {
        genreCheckboxes.forEach(checkbox -> checkbox.setState(originalSelectedGenres.contains(checkbox.getLabel())));

    }


    private void updateMovieAction() {
        if (!MovieFormValidator.isFormValid(txtTitle, genreCheckboxes)) return;
        updateMovie();
    }


    private void updateMovie() {
        db.updateMovieTitle(txtTitle.getText().trim(), MOVIE_ID);
        MovieSchema.deleteMovie(MovieTable.MOVIE_GENRES, MOVIE_ID, db);
        List<Integer> selectedGenreIds = GenreSelectionUtils.getSelectedGenres(genreCheckboxes, genres).stream().map(Genre::id).toList();
        db.addGenresToMovie(MOVIE_ID, selectedGenreIds);
        Messages.message.accept("Movie updated");
        WindowUtils.openMainMenu(this, mainMenu);

    }

    void main() {
        new EditMovieForm(mainMenu, 0);
    }
}

