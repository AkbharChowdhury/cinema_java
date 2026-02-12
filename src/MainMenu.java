import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Supplier;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import models.ButtonFactory;
import models.Genre;
import models.Messages;
import models.Movie;
import models.SearchMovies;

public class MainMenu extends JFrame implements ActionListener {
    private final MovieDatabase db = MovieDatabase.getInstance();
    private List<Movie> movies = db.fetchMovies();
    private final SearchMovies search = new SearchMovies(movies);

    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnRemove = new JButton("Remove");
    @SuppressWarnings("FieldCanBeLocal")
    private final List<JButton> buttons = List.of(btnAdd, btnEdit, btnRemove);
    private final JTextField txtTitle = new JTextField(40);
    private final JComboBox<String> comboBoxGenres = new JComboBox<>();
    private final JTable table = new JTable();
    private DefaultTableModel tableModel = new DefaultTableModel() {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final Map<Object, Runnable> componentActions = Map.of(
            btnAdd, () -> new AddMovieForm(this).setVisible(true),
            btnEdit, this::editMovieAction,
            btnRemove, this::removeMovieAction,
            comboBoxGenres, this::genreAction
    );

    private void tableProperties() {
        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        List.of("Title", "Genres").forEach(tableModel::addColumn);
        tableModel = (DefaultTableModel) table.getModel();

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private void buttonToolTips() {
        btnAdd.setToolTipText("Add a new movie with the selected genres");
        btnEdit.setToolTipText("Edit the selected movie");
        btnRemove.setToolTipText("Remove the selected movie");
    }

    public MainMenu() {
        buttonToolTips();
        tableProperties();
        comboBoxGenres.setModel(new DefaultComboBoxModel<>(new Vector<>(getGenres())));
        setResizable(true);
        setLayout(new BorderLayout());
        setSize(800, 500);
        setTitle("Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel top = new JPanel();
        top.add(new JLabel("Movie: "));
        top.add(txtTitle);
        top.add(new JLabel("Genre"));
        top.add(comboBoxGenres);

        JPanel middle = new JPanel();
        middle.add(new JScrollPane(table));
        JPanel south = new JPanel();
        buttons.forEach(south::add);

        add(BorderLayout.NORTH, top);
        add(BorderLayout.CENTER, middle);
        add(BorderLayout.SOUTH, south);

        ButtonFactory.applyHandCursor.accept(buttons);
        comboBoxGenres.addActionListener(this);
        buttons.forEach(button -> button.addActionListener(this));

        populateList();

        txtTitle.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                search.setTitle(txtTitle.getText());
                populateList();
            }
        });
        setVisible(true);
    }

    private List<String> getGenres() {
        List<String> genres = new ArrayList<>(db.fetchAvailableGenres());
        genres.addFirst(Genre.anyGenre.get());
        return genres;
    }

    void main() {

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        MainMenuState.setHasOpenMainMenu(true);
        var source = e.getSource();
        Runnable action = componentActions.get(source);
        if (action != null) action.run();
    }

    private void genreAction() {
        search.setGenre(comboBoxGenres.getSelectedItem().toString());
        populateList();
    }

    private void editMovieAction() {
        if (isSelectionRequired.get()) {
            showMovieRequiredMessage();
            return;
        }
        int movieId = getSelectedMovieId();
        new EditMovieForm(this, movieId).setVisible(true);

    }

    private void removeMovieAction() {
        if (isSelectionRequired.get()) {
            showMovieRequiredMessage();
            return;
        }
        int movieId = getSelectedMovieId();
        removeMovie(movieId);
    }

    private final Supplier<Boolean> isSelectionRequired = () -> table.getSelectedRow() == -1;

    private int getSelectedMovieId() {
        int selectedIndex = table.getSelectedRow();
        movies = search.filter();
        return movies.get(selectedIndex).id();
    }

    private void showMovieRequiredMessage() {
        Messages.showErrorMessage.accept("No Selection", "Please select a movie");
    }


    private void removeMovie(int movieId) {
        if (isSelectionRequired.get()) {
            showMovieRequiredMessage();
            return;
        }
        if (!Messages.hasConfirmed.apply("Are you sure you want to remove this movie?")) return;
        deleteMovie(movieId);

    }

    private void deleteMovie(int movieId) {
        boolean deleted = MovieSchema.deleteMovie(MovieSchema.MOVIE_TABLE, movieId, db);
        if (!deleted) {
            Messages.showErrorMessage.accept("Movie error", "Cannot delete movie");
            return;
        }
        search.setMovies(db.fetchMovies());
        populateList();
    }


    private void populateList() {
        tableModel.setRowCount(0);
        search.filter().forEach(this::addMovieRow);

    }

    private void addMovieRow(Movie movie) {
        tableModel.addRow(new Object[]{
                movie.title(),
                movie.genres()
        });
    }


}


