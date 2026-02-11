import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JFrame;
import java.util.function.Consumer;

public final class WindowUtils {


    /**
     * Apply autofocus to a component after the EventQueue processes the event.
     */
    public static final Consumer<Component> applyAutofocus = (c) -> EventQueue.invokeLater(c::requestFocus);

    /**
     * Closes the current frame and opens the MainMenu frame.
     *
     * @param currentFrame  The JFrame that should be closed
     * @param mainMenuFrame The existing MainMenu frame (if any)
     */
    public static void openMainMenu(JFrame currentFrame, JFrame mainMenuFrame) {
        // Close existing main menu if it exists
        if (mainMenuFrame != null) mainMenuFrame.dispose();

        // Close the current frame
        if (currentFrame != null) currentFrame.dispose();

        // Open a new MainMenu
        new MainMenu();  // Ensure you call setVisible(true) in the MainMenu constructor if necessary
    }
}
