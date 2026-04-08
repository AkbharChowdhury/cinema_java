import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import models.ButtonFactory;
import models.Genre;
import models.GenreCheckboxFactory;
import models.GenreSelectionUtils;
import models.Messages;
import models.MovieFormValidator;
import models.PanelFactory;

public class AddMovieForm extends JFrame {
    private static MainMenu mainMenu;
    private final MovieDatabase db;
    private final List<Genre> genres;
    private final JTextField txtTitle = new JTextField(20);
    private final JButton btnAddMovie = ButtonFactory.createButton("Add Movie", _ -> handleAddMovie());
    private final List<Checkbox> genreCheckboxes;

    public AddMovieForm(MainMenu mainMenuForm) {
        db = MovieDatabase.getInstance();
        genres = db.fetchAllGenres();
        mainMenu = mainMenuForm;

//
        JPanel top = PanelFactory.leftFlowPanelWithPadding();

        JPanel middle = new JPanel();

        btnAddMovie.setToolTipText("Add a new movie with the selected genres");
        setTitle("Add Movie");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        top.add(new JLabel("Movie"));
        top.add(txtTitle);
        middle.setLayout(new GridLayout(genres.size(), 2));

        genreCheckboxes = GenreCheckboxFactory.create(genres);
        genreCheckboxes.forEach(middle::add);

        panel.add(top, BorderLayout.NORTH);
        panel.add(middle, BorderLayout.CENTER);
        panel.add(btnAddMovie, BorderLayout.SOUTH);


//        panel.add(bottom, BorderLayout.SOUTH);

        setContentPane(panel);
        setDefaultCloseOperation(MainMenuState.getCloseOperation());

        setSize(400, 400);
        WindowUtils.applyAutofocus.accept(txtTitle);
        setVisible(true);

    }

    private void handleAddMovie() {
        if (!MovieFormValidator.validateForm(txtTitle, genreCheckboxes)) return;
        List<Genre> selectedGenres = GenreSelectionUtils.getSelectedGenres(genreCheckboxes, genres);
        List<Integer> selectedGenreIds = GenreSelectionUtils.getSelectedGenreIds(selectedGenres);
        boolean hasAddedMovie = db.addMovieWithGenres(txtTitle.getText().trim(), new HashSet<>(selectedGenreIds));
        if (!hasAddedMovie) {
            Messages.showError.accept("Could not add movie", "There was an error adding the movie");
            return;
        }
        Messages.message.accept("Movie Added");
        WindowUtils.openMainMenu(this, mainMenu);
    }


    static void main() {
        new AddMovieForm(mainMenu);
    }

}

