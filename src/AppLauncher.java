import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // delay our weather app GUI
                new AbohawaGUI().setVisible(true);

            }

        });

    }
}
