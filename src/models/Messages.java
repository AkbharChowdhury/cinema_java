package models;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Messages {
    private Messages() {

    }

    public static void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }


    public static Consumer<String> printErrorMessage = System.err::println;

    public static Function<String, Boolean> hasConfirmed = (msg) -> JOptionPane.showConfirmDialog(null, msg) == JOptionPane.YES_OPTION;
    public static void message(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

}
