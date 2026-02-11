import javax.swing.WindowConstants;

public final class MainMenuState {

    // Track if MainMenu is open
    private static boolean hasOpenMainMenu;

    private MainMenuState() {
    }

    /**
     * Sets whether the MainMenu is currently open.
     */
    public static void setHasOpenMainMenu(boolean open) {
        hasOpenMainMenu = open;
    }

    /**
     * Returns the appropriate JFrame close operation depending on whether
     * the MainMenu is open.
     */
    public static int getCloseOperation() {
        return hasOpenMainMenu ? WindowConstants.DISPOSE_ON_CLOSE : WindowConstants.EXIT_ON_CLOSE;
    }
    private static boolean isMainMenuOpen() {
        return hasOpenMainMenu;
    }


}