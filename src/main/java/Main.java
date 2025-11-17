
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import vista.VentanaPrincipal;
import javax.swing.SwingUtilities;

/**
 *
 * @author omarf
 */
public class Main {
    public static void main(String[] args) {
        // Inicializar la base de datos
        modelo.ConexionSQLite.inicializarBD();
        modelo.ConexionSQLiteDevolver.inicializarBD();

        // Mostrar la ventana principal
        SwingUtilities.invokeLater(() -> {
            new vista.VentanaPrincipal().setVisible(true);
        });
    }
}
