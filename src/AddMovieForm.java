
import models.Genre;
import models.Messages;
import models.MyButton;
import models.MyWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class AddMovieForm extends JFrame implements ActionListener {
    private static MainMenu mainMenu;
    private final MovieDatabase db = MovieDatabase.getInstance();
    private final List<Genre> genres = Collections.unmodifiableList(db.fetchAllGenres());
    private final JTextField txtTitle = new JTextField(20);
    private final JButton btnAddMovie = new JButton("Add Movie");
    private final List<Checkbox> genreCheckboxes;

    public AddMovieForm(MainMenu mainMenuForm) {
        btnAddMovie.setToolTipText("Add a new movie with the selected genres");
        mainMenu = mainMenuForm;
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
        setDefaultCloseOperation(MyWindow.getCloseOperation());
        setSize(400, 400);
        btnAddMovie.addActionListener(this);
        MyButton.applyHandCursor.accept(new JButton[]{btnAddMovie});


        autofocus();
        setVisible(true);

    }


    private void autofocus() {
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                txtTitle.requestFocus();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean hasSelectedGenre = Genre.hasSelectedGenre.apply(genreCheckboxes);
        if (txtTitle.getText().trim().isBlank()) {
            Messages.showErrorMessage("Title is Empty", "Please enter a movie title");
            return;
        }
        if (!hasSelectedGenre) {
            Messages.showErrorMessage("Missing Genre", "Please select at least one genre");
            return;
        }

        List<Integer> selectedGenres = Genre.getSelectedGenres.apply(genreCheckboxes, genres).stream().map(Genre::id).toList();
        boolean hasAddedMovie = db.addMovieWithGenres(txtTitle.getText().trim(), new HashSet<>(selectedGenres));
        if (!hasAddedMovie) {
            Messages.showErrorMessage("", "There was an error adding the movie");
            return;
        }

        clearForm();
        Messages.message("Movie Added");
        redirectToMainMenu();
    }

    private void redirectToMainMenu() {
        if (mainMenu != null) mainMenu.dispose();
        dispose();
        new MainMenu();
    }

    private void clearForm() {
        txtTitle.setText("");
        genreCheckboxes.forEach(checkbox -> checkbox.setState(false));
    }


    static void main() {
        new AddMovieForm(mainMenu);
    }

}

