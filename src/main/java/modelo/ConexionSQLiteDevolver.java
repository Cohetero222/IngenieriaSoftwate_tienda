package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLiteDevolver {

    private static final String URL = "jdbc:sqlite:deudores.db";

    // ===============================
    //  MÉTODO DE CONEXIÓN CORREGIDO
    // ===============================
    public static Connection conectar() throws SQLException {
        try {
            // 🔥 ESTA LÍNEA ES OBLIGATORIA PARA EVITAR EL ERROR DE DRIVER
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error cargando el driver SQLite: " + e.getMessage());
        }

        return DriverManager.getConnection(URL);
    }

    // ======================================
    //  INICIALIZAR LA BASE Y CREAR TABLAS
    // ======================================
    public static void inicializarBD() {

        String sqlDeudores = """
                CREATE TABLE IF NOT EXISTS deudores (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    producto TEXT,
                    marca TEXT,
                    categoria TEXT NOT NULL,
                    cantidad INTEGER DEFAULT 0,
                    precio REAL,
                    costo REAL,
                    pagado INTEGER DEFAULT 0
                );
                """;

        try (Connection conn = conectar();
     Statement stmt = conn.createStatement()) {

    // Crear tabla si no existe
    stmt.execute(sqlDeudores);

    // 🔥 Añadir columna pagado si no existe
    stmt.execute("ALTER TABLE deudores ADD COLUMN pagado INTEGER DEFAULT 0;");
    
} catch (SQLException e) {
    // Ignorar si la columna ya existe
    if (!e.getMessage().contains("duplicate column name")) {
        System.err.println("Error actualizando tabla: " + e.getMessage());
    }
}
    }
}
