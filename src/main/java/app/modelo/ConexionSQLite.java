package app.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLite {

    private static final String URL = "jdbc:sqlite:productos.db";

    // ===============================
    //  MÉTODO DE CONEXIÓN CORREGIDO
    // ===============================
    public static Connection conectar() throws SQLException {
        try {
            // ESTA LÍNEA ES OBLIGATORIA PARA QUE FUNCIONE SQLITE
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

        String sqlProductos = """
                CREATE TABLE IF NOT EXISTS productos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    marca TEXT,
                    categoria TEXT NOT NULL,
                    cantidad INTEGER DEFAULT 0,
                    estado TEXT CHECK(estado IN ('Disponible', 'Agotado', 'Caducado', 'Descontinuado')),
                    ventas INTEGER DEFAULT 0,
                    fecha_caducidad DATE,
                    precio REAL,
                    costo REAL
                );
                """;

        String sqlVentas = """
                CREATE TABLE IF NOT EXISTS ventas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_producto INTEGER NOT NULL,
                    cantidad INTEGER NOT NULL,
                    precio_unitario REAL NOT NULL,
                    costo REAL NOT NULL,
                    fecha TEXT NOT NULL,
                    FOREIGN KEY (id_producto) REFERENCES productos(id)
                );
                """;

        String sqlAlterProductos =
                "ALTER TABLE productos ADD COLUMN costo REAL DEFAULT 0;";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            // Crear tablas
            stmt.execute(sqlProductos);
            stmt.execute(sqlVentas);

            // Intentar añadir columna "costo"
            try {
                stmt.execute(sqlAlterProductos);
            } catch (SQLException alterEx) {
                if (!alterEx.getMessage().contains("duplicate column name")) {
                    throw alterEx;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
    // ======================================
    //  MÉTODO PARA REORGANIZAR IDs
    // ======================================
    public static void reorganizarIDsProductos() throws SQLException {
        Connection conn = null;
        try {
            conn = conectar();
            conn.setAutoCommit(false);

            // 1. Crear tabla temporal
            String crearTemp = """
                CREATE TEMPORARY TABLE temp_productos AS 
                SELECT * FROM productos ORDER BY id;
                """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(crearTemp);
            }

            // 2. Eliminar todos los registros de la tabla original
            String limpiarOriginal = "DELETE FROM productos;";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(limpiarOriginal);
            }

            // 3. Reinsertar con nuevos IDs secuenciales
            String insertarNuevos = """
                INSERT INTO productos (nombre, marca, categoria, cantidad, estado, ventas, fecha_caducidad, precio, costo)
                SELECT nombre, marca, categoria, cantidad, estado, ventas, fecha_caducidad, precio, costo
                FROM temp_productos ORDER BY id;
                """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(insertarNuevos);
            }

            // 4. Eliminar tabla temporal
            String eliminarTemp = "DROP TABLE temp_productos;";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(eliminarTemp);
            }

            // 5. Resetear la secuencia AUTOINCREMENT
            String resetSequence = "DELETE FROM sqlite_sequence WHERE name='productos';";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(resetSequence);
            }

            conn.commit();
            System.out.println("IDs de productos reorganizados exitosamente");

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
