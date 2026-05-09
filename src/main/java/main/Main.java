package main;

import vista.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar LookAndFeel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
