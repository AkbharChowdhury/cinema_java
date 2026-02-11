package models;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import java.util.List;


public class ButtonFactory {
    private ButtonFactory() {
    }

    public static JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        if (listener != null) {
            button.addActionListener(listener);
        }
        return button;
    }

    public static Consumer<List<JButton>> applyHandCursor = (buttons) ->
            buttons.forEach(button -> button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)));
}
