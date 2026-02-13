import javax.swing.WindowConstants;

/**
 * Tracks whether the MainMenu is currently open and provides
 * the correct JFrame close operation based on that state.
 */
public final class MainMenuState {

    private static volatile boolean mainMenuOpen;

    private MainMenuState() {}

    /** Sets whether the main menu is currently open. */
    public static void setMainMenuOpen(boolean open) {
        mainMenuOpen = open;
    }

    /** Returns the JFrame close operation based on main menu state. */
    public static int getCloseOperation() {
        return mainMenuOpen ? WindowConstants.DISPOSE_ON_CLOSE : WindowConstants.EXIT_ON_CLOSE;
    }
}