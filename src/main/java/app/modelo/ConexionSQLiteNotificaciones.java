package app.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLiteNotificaciones {

    //Nombre del archivo donde se guardará la tabla notificaciones
    private static final String URL = "jdbc:sqlite:notificaciones.db";

    // ===============================
    //  MÉTODO DE CONEXIÓN
    // ===============================
    public static Connection conectar() throws SQLException {
        /*try {
            Class.forName("org.sqlite.JDBC"); // Obligatorio para SQLite
        } catch (ClassNotFoundException e) {
            System.err.println("Error cargando el driver SQLite: " + e.getMessage());
        }*/

        return DriverManager.getConnection(URL);
    }

    // ======================================
    //  INICIALIZAR BASE Y CREAR TABLA
    // ======================================
    public static void inicializarBD() {

        String sqlNotificaciones = """
                CREATE TABLE IF NOT EXISTS notificaciones (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    producto_id INTEGER NOT NULL,
                    producto_nombre TEXT NOT NULL,
                    cantidad_actual INTEGER NOT NULL,
                    cantidad_minima INTEGER NOT NULL,
                    fecha_creacion TEXT NOT NULL,
                    revisada INTEGER DEFAULT 0
                );
                """;

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlNotificaciones);

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}

