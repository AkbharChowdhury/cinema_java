package models;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public final class PanelFactory {

    public static JPanel leftFlowPanelWithPadding() {
        int hGap = 10;
        int vGap = 5;
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, hGap, vGap));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        return panel;
    }
}
