package models;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Messages {
    private Messages() {
    }

    public static BiConsumer<String, String> showErrorMessage = (title, message) -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    public static Consumer<String> printErrorMessage = System.err::println;
    public static Function<String, Boolean> hasConfirmed = (message) -> JOptionPane.showConfirmDialog(null, message) == JOptionPane.YES_OPTION;
    public static Consumer<String> message = (message) -> JOptionPane.showMessageDialog(null, message);

}
