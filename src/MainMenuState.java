import lombok.Setter;

import javax.swing.WindowConstants;
import java.util.function.Supplier;

public final class MainMenuState {

    /**
     * -- SETTER --
     * Sets whether the MainMenu is currently open.
     */
    // Track if MainMenu is open
    @Setter
    private static boolean hasOpenMainMenu;

    private MainMenuState() {
    }

    /**
     * Returns the appropriate JFrame close operation depending on whether
     * the MainMenu is open.
     */
    public static Supplier<Integer> getCloseOperation = () -> hasOpenMainMenu ? WindowConstants.DISPOSE_ON_CLOSE : WindowConstants.EXIT_ON_CLOSE;

}