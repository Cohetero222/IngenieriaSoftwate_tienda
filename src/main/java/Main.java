import app.vista.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        app.modelo.ConexionSQLite.inicializarBD();
        app.modelo.ConexionSQLiteDevolver.inicializarBD();

        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}
