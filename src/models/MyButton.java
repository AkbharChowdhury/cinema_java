package models;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

public class MyButton {
    private MyButton() {
    }

    public static Consumer<JButton[]> applyHandCursor = (buttons) -> Arrays.stream(buttons).forEach(button -> button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)));
}
