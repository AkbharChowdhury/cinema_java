
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
import java.util.HashSet;
import java.util.List;

public class AddMovieForm extends JFrame implements ActionListener {
    private static MainMenu mainMenu;
    private final Database db = Database.getInstance();
    private final List<Genre> genres = db.getAllGenres();
    private final JTextField txtTitle = new JTextField(20);
    private final JButton btnAddMovie = new JButton("Add Movie");
    private final List<Checkbox> checkboxes;

    public AddMovieForm(MainMenu mainMenuForm) {
        mainMenu = mainMenuForm;
        setTitle("Add Movie");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel middle = new JPanel();

        JPanel top = new JPanel();
        top.add(new JLabel("Movie"));
        top.add(txtTitle);
        middle.setLayout(new GridLayout(genres.size(), 2));

        checkboxes = genres.stream().map(genre -> new Checkbox(genre.name())).toList();
        checkboxes.forEach(middle::add);

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
        boolean hasSelectedGenre = Genre.hasSelectedGenre.apply(checkboxes);
        if (txtTitle.getText().trim().isBlank()) {
            Messages.showErrorMessage("", "Movie title is required");
            return;
        }
        if (!hasSelectedGenre) {
            Messages.showErrorMessage("", "Please choose a genre");
            return;
        }

//        List<String> selectedGenres = checkboxes.stream().filter(Checkbox::getState).map(Checkbox::getLabel).toList();
        List<Integer> selectedGenreIDs = Genre.getSelectedGenres(checkboxes, genres).stream().map(Genre::id).toList();
        boolean hasAddedMovie = db.addMovieAndGenres(txtTitle.getText().trim(), new HashSet<>(selectedGenreIDs));
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
        checkboxes.forEach(checkbox -> checkbox.setState(false));
    }


    public static void main(String[] args) {
        new AddMovieForm(mainMenu);
    }

}

