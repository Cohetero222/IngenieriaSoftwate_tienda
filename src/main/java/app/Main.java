package app;

import javax.swing.SwingUtilities;

/**
 *
 * @author omarf
 */
public class Main {
    public static void main(String[] args) {
        // Inicializar la base de datos
        app.modelo.ConexionSQLite.inicializarBD();
        app.modelo.ConexionSQLiteDevolver.inicializarBD();

        // Mostrar la ventana principal
        SwingUtilities.invokeLater(() -> {
            new app.vista.VentanaPrincipal().setVisible(true);
        });
    }
}
