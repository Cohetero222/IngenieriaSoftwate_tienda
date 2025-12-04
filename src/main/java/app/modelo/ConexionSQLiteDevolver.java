package app.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLiteDevolver {

    private static final String URL = "jdbc:sqlite:deudores.db";

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error cargando el driver SQLite: " + e.getMessage());
        }

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
                    costo REAL,
                    pagado INTEGER DEFAULT 0
                );
                """;

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            // Crear tabla si no existe
            stmt.execute(sqlDeudores);

            // Agregar columna pagado si no existe
            stmt.execute("ALTER TABLE deudores ADD COLUMN pagado INTEGER DEFAULT 0;");

        } catch (SQLException e) {

            // Ignorar si la columna ya existe
            if (!e.getMessage().contains("duplicate column name")) {
                System.err.println("Error actualizando tabla: " + e.getMessage());
            }
        }
    }

    // ======================================
    //  MÉTODO PARA REORGANIZAR IDs DE DEUDORES
    // ======================================
    public static void reorganizarIDsDeudores() throws SQLException {
        Connection conn = null;
        try {
            conn = conectar();
            conn.setAutoCommit(false);

            // 1. Crear tabla temporal
            String crearTemp = """
                CREATE TEMPORARY TABLE temp_deudores AS 
                SELECT * FROM deudores ORDER BY id;
                """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(crearTemp);
            }

            // 2. Vaciar la tabla original
            String limpiarOriginal = "DELETE FROM deudores;";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(limpiarOriginal);
            }

            // 3. Reinsertar con nuevos IDs secuenciales
            String insertarNuevos = """
                INSERT INTO deudores (nombre, producto, marca, categoria, cantidad, precio, costo, pagado)
                SELECT nombre, producto, marca, categoria, cantidad, precio, costo, pagado
                FROM temp_deudores ORDER BY id;
                """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(insertarNuevos);
            }

            // 4. Eliminar tabla temporal
            String eliminarTemp = "DROP TABLE temp_deudores;";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(eliminarTemp);
            }

            // 5. Resetear autoincremento
            String resetSequence = "DELETE FROM sqlite_sequence WHERE name='deudores';";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(resetSequence);
            }

            conn.commit();
            System.out.println("IDs de deudores reorganizados exitosamente");

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}
