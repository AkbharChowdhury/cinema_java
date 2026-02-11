package models;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

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


    /**
     * Closes the current frame, disposes the main menu if it's open,
     * and opens a new MainMenu instance.
     *
     * @param mainMenu     the existing main menu JFrame (can be null)
     * @param currentFrame the current JFrame that should be closed
     * @return the new MainMenu instance
     */
    public static void redirectToMainMenu(JFrame mainMenu, JFrame currentFrame) {
//        if (mainMenu != null) mainMenu.dispose(); // close old main menu if open
//        currentFrame.dispose(); // close current frame
//        return new MainMenu(); // create and return new main menu


        if (mainMenu != null) mainMenu.dispose();
        currentFrame.dispose();

    }

    public static void openNewFrame(JFrame currentFrame, Supplier<? extends JFrame> frameSupplier) {
        if (currentFrame != null) currentFrame.dispose();
        JFrame newFrame = frameSupplier.get();
        newFrame.setVisible(true);
    }

}
