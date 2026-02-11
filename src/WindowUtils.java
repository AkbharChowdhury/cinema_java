import java.awt.*;
import java.util.function.Consumer;
import javax.swing.JFrame;

public class WindowUtils {

    private static boolean hasOpenMainMenu;
    public static void setHasOpenMainMenu(boolean open) {
        hasOpenMainMenu = open;
    }

    public static int getCloseOperation() {
        return hasOpenMainMenu ? JFrame.DISPOSE_ON_CLOSE : JFrame.EXIT_ON_CLOSE;
    }

    public static Consumer<Component> applyAutofocus = (c) -> EventQueue.invokeLater(c::requestFocus);


    /**
     * Closes the current frame and opens a new MainMenu.
     *
     * @param currentFrame The JFrame that should be closed
     */
    public static void openMainMenu(JFrame currentFrame, JFrame mainMenuFrame) {
        // Close existing main menu if it exists
        if (mainMenuFrame != null) mainMenuFrame.dispose();
        // Close the current frame
        currentFrame.dispose();
        new MainMenu();


    }


}
