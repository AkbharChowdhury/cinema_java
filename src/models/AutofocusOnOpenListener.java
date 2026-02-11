package models;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AutofocusOnOpenListener extends WindowAdapter {
    private final Component componentToFocus;

    public AutofocusOnOpenListener(Component componentToFocus) {
        this.componentToFocus = componentToFocus;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        componentToFocus.requestFocus();
    }


}
