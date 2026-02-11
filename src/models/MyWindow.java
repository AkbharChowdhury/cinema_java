package models;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MyWindow {

    private static boolean hasOpenMainMenu;


    public static void setHasOpenMainMenu(boolean hasOpenMainMenu) {
        MyWindow.hasOpenMainMenu = hasOpenMainMenu;
    }

    public static boolean hasOpenMainMenu() {
        return hasOpenMainMenu;
    }

    public static int getCloseOperation() {
        return hasOpenMainMenu ? JFrame.DISPOSE_ON_CLOSE : JFrame.EXIT_ON_CLOSE;
    }

    public static Consumer<Component> applyAutofocus = (c) -> EventQueue.invokeLater(c::requestFocus);

}
