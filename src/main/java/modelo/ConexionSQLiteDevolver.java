package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLiteDevolver {
    private static final String URL = "jdbc:sqlite:deudores.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

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
                    costo REAL
                );
                """;

        try (Connection conn = conectar();
                Statement stmt = conn.createStatement()) {

            // 1. Crear las tablas (si no existen)
            stmt.execute(sqlDeudores);

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }

}
