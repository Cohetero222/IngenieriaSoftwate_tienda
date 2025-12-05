import app.vista.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Inicializar la base de datos
        app.modelo.ConexionSQLite.inicializarBD();
        app.modelo.ConexionSQLiteDevolver.inicializarBD();
        app.modelo.ConexionSQLiteNotificaciones.inicializarBD();

        // Mostrar la ventana principal
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}