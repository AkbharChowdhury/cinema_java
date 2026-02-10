import models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

public class MainMenu extends JFrame implements ActionListener {
    private final MovieDatabase db = MovieDatabase.getInstance();
    private List<Movie> movies = db.fetchMovies();
    private final SearchMovies search = new SearchMovies(movies);

    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnRemove = new JButton("Remove");
    private final JButton[] buttons = {btnAdd, btnEdit, btnRemove};

    private final JTextField txtTitle = new JTextField(40);
    private final JComboBox<String> comboBoxGenres = new JComboBox<>();
    private final JTable table = new JTable();
    private DefaultTableModel tableModel = new DefaultTableModel() {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };


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
        Arrays.stream(buttons).forEach(south::add);

        add(BorderLayout.NORTH, top);
        add(BorderLayout.CENTER, middle);
        add(BorderLayout.SOUTH, south);

        MyButton.applyHandCursor.accept(buttons);
        comboBoxGenres.addActionListener(this);
        Arrays.stream(buttons).forEach(button -> button.addActionListener(this));

        populateList();
        setVisible(true);

        txtTitle.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                search.setTitle(txtTitle.getText());
                populateList();
            }
        });
    }

    private List<String> getGenres() {
        List<String> genres = new ArrayList<>(db.fetchAvailableGenres());
        genres.addFirst(Genre.anyGenre.get());
        return genres;
    }

    void main() {
        new MainMenu();

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        MyWindow.setHasOpenMainMenu(true);
        Object source = e.getSource();
        if (source == btnAdd) new AddMovieForm(this);
        if (source == btnEdit) editMovie(getSelectedMovieId());
        if (source == btnRemove) removeMovie(getSelectedMovieId());
        if (source == comboBoxGenres) {
            search.setGenre(comboBoxGenres.getSelectedItem().toString());
            populateList();
        }

    }

    private final Supplier<Boolean> isSelectionRequired = () -> table.getSelectedRow() == -1;


    private void editMovie(int movieId) {

        if (isSelectionRequired.get()) {
            showMovieRequiredMessage();
            return;
        }
        MovieInfo.setMovieID(movieId);
        new EditMovieForm(MainMenu.this);
    }

    private int getSelectedMovieId() {
        int selectedIndex = table.getSelectedRow();
        movies = search.filterMovies.get();
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
        refreshMovieList();
    }

    private void deleteMovie(int id) {
        db.deleteRecord(MovieSchema.MOVIE_TABLE, MovieSchema.MOVIE_ID, id);
        tableModel.removeRow(table.getSelectedRow());
    }

    private void refreshMovieList() {
        search.setList(db.fetchMovies());
        populateList();
    }


    private void populateList() {
        tableModel.setRowCount(0);
        search.filterMovies.get().forEach(this::addMovieRow);

    }

    private void addMovieRow(Movie movie) {
        tableModel.addRow(new Object[]{
                movie.title(),
                movie.genres()
        });
    }


}


