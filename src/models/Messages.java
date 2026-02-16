package models;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.JOptionPane;

public final class Messages {

    private Messages() {
    }

    public static final BiConsumer<String, String> showError =
            (title, message) ->
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

    public static final Consumer<String> printErrorMessage =
            System.err::println;

    public static final Function<String, Boolean> hasConfirmed =
            message -> JOptionPane.showConfirmDialog(null, message)
                    == JOptionPane.YES_OPTION;

    public static final Consumer<String> message =
            message -> JOptionPane.showMessageDialog(null, message);
}
