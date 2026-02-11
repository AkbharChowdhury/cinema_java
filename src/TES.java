import javax.swing.*;

public class TES {
    /**
     * Closes the current frame and opens a new MainMenu.
     *
     * @param currentFrame The JFrame that should be closed
     */
    public static void openMainMenu(JFrame currentFrame) {
        if (currentFrame != null) currentFrame.dispose(); // close current frame
        MainMenu mainMenu = new MainMenu();                // create main menu from models package
        mainMenu.setVisible(true);                         // show it
    }
}
