package defensatorres;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada del simulador. */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception excepcion) {
                System.err.println("Se utilizará el estilo visual predeterminado.");
            }
            new VentanaJuego().setVisible(true);
        });
    }
}
