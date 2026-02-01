import enums.MovieRow;

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
    private final DefaultTableModel tableModel = new DefaultTableModel() {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };


    private void tableProperties() {

        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredSize(new Dimension(700, 600));
        List.of("Title", "Genres").forEach(tableModel::addColumn);
    }


    public MainMenu() {
        btnAdd.setToolTipText("Add a new movie with the selected genres");
        btnEdit.setToolTipText("Edit the selected movie");
        btnRemove.setToolTipText("Remove the selected movie");

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
        genres.addFirst("Any");
        return genres;
    }

    void main() {
        new MainMenu();

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        MyWindow.setHasOpenMainMenu(true);
        if (e.getSource() == btnEdit) editMovie();
        if (e.getSource() == btnAdd) new AddMovieForm(this);
        if (e.getSource() == btnRemove) removeMovie();
        if (e.getSource() == comboBoxGenres) {
            search.setGenre(comboBoxGenres.getSelectedItem().toString());
            populateList();
        }

    }

    private final Supplier<Boolean> isSelectionRequired = () -> table.getSelectedRow() == -1;


    private void editMovie() {

        if (isSelectionRequired.get()) {
            showMovieRequiredMessage();
            return;
        }
        MovieInfo.setMovieID(getSelectedMovieID());
        new EditMovieForm(MainMenu.this);
    }

    private int getSelectedMovieID() {
        int selectedIndex = table.getSelectedRow();
        movies = search.filterMovies.get();
        return movies.get(selectedIndex).id();
    }

    private void showMovieRequiredMessage() {
        Messages.showErrorMessage("No Selection", "Please select a movie");
    }

    private void removeMovie() {

        if (isSelectionRequired.get()) {
            showMovieRequiredMessage();
            return;
        }

        if (Messages.hasConfirmed.apply("Are you sure you want to remove this movie?")) {
            db.deleteRecord(MovieSchema.MOVIE_TABLE, MovieSchema.MOVIE_ID, getSelectedMovieID());
            tableModel.removeRow(table.getSelectedRow());
            search.setList(db.fetchMovies());
        }
    }


    private void populateList() {
        ((DefaultTableModel) table.getModel()).setRowCount(0);
        List<Movie> filteredMovies = search.filterMovies.get();
        final int TOTAL_NUM_MOVIES = filteredMovies.size();
        for (int i = 0; i < TOTAL_NUM_MOVIES; i++) {
            tableModel.addRow(new Object[0]);
            Movie movie = filteredMovies.get(i);
            tableModel.setValueAt(movie.title(), i, MovieRow.TITLE.ordinal());
            tableModel.setValueAt(movie.genres(), i, MovieRow.GENRE.ordinal());
        }
    }


}


