import enums.MovieEnum;

import models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;


public class MainMenu extends JFrame implements ActionListener {
    private final Database db = Database.getInstance();
    private List<Movie> movieList = db.getMovieList();
    private final SearchMovies search = new SearchMovies(movieList);

    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnRemove = new JButton("Remove");
    private JButton[] buttons = {btnAdd, btnEdit, btnRemove};

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

    List<String> getGenres() {
        List<String> genres = new ArrayList<>(db.getMovieGenres());
        genres.addFirst("Any");
        return genres;
    }

    void main() {new MainMenu();}




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

    private boolean isSelectionRequired() {
        return table.getSelectedRow() == -1;
    }

    private void editMovie() {

        if (isSelectionRequired()) {
            showMovieRequiredMessage();
            return;
        }
        MovieInfo.setMovieID(getSelectedMovieID());
        new EditMovieForm(MainMenu.this);
    }

    int getSelectedMovieID() {
        int selectedIndex = table.getSelectedRow();
        movieList = search.filterResults.get();
        return movieList.get(selectedIndex).id();
    }

    void showMovieRequiredMessage() {
        Messages.showErrorMessage("Movie required!", "Please select a movie!");
    }

    void removeMovie() {

        if (isSelectionRequired()) {
            showMovieRequiredMessage();
            return;
        }

        if (Messages.hasConfirmed.apply("Are you sure you want to remove this movie?")) {
            db.deleteRecord("movies", "movie_id", getSelectedMovieID());
            tableModel.removeRow(table.getSelectedRow());
            search.setList(db.getMovieList());
        }
    }


    void populateList() {
        ((DefaultTableModel) table.getModel()).setRowCount(0);
        List<Movie> movies = search.filterResults.get();
        int movieSize = movies.size();
        for (int i = 0; i < movieSize; i++) {
            Movie movie = movies.get(i);
            tableModel.addRow(new Object[0]);
            tableModel.setValueAt(movie.title(), i, MovieEnum.TITLE.getValue());
            tableModel.setValueAt(movie.genres(), i, MovieEnum.GENRE.getValue());


        }

    }


}


