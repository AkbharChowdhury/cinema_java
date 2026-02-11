package models;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.List;


public class MyButton {
    private MyButton() {
    }

    public static Consumer<List<JButton>> applyHandCursor = (buttons) ->
            buttons.forEach(button -> button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)));
}
