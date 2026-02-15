package models;

import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.JButton;


public final class ButtonFactory {
    private ButtonFactory() {
    }

    private static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    public static JButton createButton(String text, ActionListener listener) {
        Objects.requireNonNull(text, "Button text must not be null");

        JButton button = new JButton(text);
        button.setCursor(HAND_CURSOR);
        if (listener != null) {
            button.addActionListener(listener);
        }
        return button;
    }

    public static final Consumer<List<JButton>> applyHandCursor = (buttons) ->
            buttons.forEach(button -> button.setCursor(HAND_CURSOR));
}
