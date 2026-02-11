import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.JFrame;

public class WindowUtils {

    private static boolean hasOpenMainMenu;


    public static void setHasOpenMainMenu(boolean hasOpenMainMenu) {
        WindowUtils.hasOpenMainMenu = hasOpenMainMenu;
    }

    public static boolean hasOpenMainMenu() {
        return hasOpenMainMenu;
    }

    public static int getCloseOperation() {
        return hasOpenMainMenu ? JFrame.DISPOSE_ON_CLOSE : JFrame.EXIT_ON_CLOSE;
    }

    public static Consumer<Component> applyAutofocus = (c) -> EventQueue.invokeLater(c::requestFocus);


    public static void openNewFrame(JFrame currentFrame, Supplier<? extends JFrame> frameSupplier) {
        if (currentFrame != null) currentFrame.dispose();
        JFrame newFrame = frameSupplier.get();
        newFrame.setVisible(true);
    }


    /**
     * Closes the current frame and opens a new MainMenu.
     *
     * @param currentFrame The JFrame that should be closed
     */
    public static void openMainMenu(JFrame currentFrame, JFrame mainMenuFrame) {
        // Close existing main menu if it exists
        if (mainMenuFrame != null) mainMenuFrame.dispose();

        // Close the current frame
        if (currentFrame != null) currentFrame.dispose();

        new MainMenu();
    }


}
