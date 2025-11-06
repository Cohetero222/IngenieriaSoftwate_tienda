package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLite {
    private static final String URL = "jdbc:sqlite:productos.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

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

        // Comando SQL para añadir la columna 'costo' a la tabla existente 'productos'
        // Esto resuelve el problema de las bases de datos creadas previamente.
        // Si la columna ya existe, SQLite lo manejará sin errores fatales en muchas
        // implementaciones,
        // pero es más robusto usar un try-catch solo para el ALTER TABLE.
        String sqlAlterProductos = "ALTER TABLE productos ADD COLUMN costo REAL DEFAULT 0;";

        try (Connection conn = conectar();
                Statement stmt = conn.createStatement()) {

            // 1. Crear las tablas (si no existen)
            stmt.execute(sqlProductos);
            stmt.execute(sqlVentas);

            // 2. Intentar añadir la columna 'costo'
            // Esto solo se ejecutará si la columna no estaba presente inicialmente.
            try {
                stmt.execute(sqlAlterProductos);
            } catch (SQLException alterEx) {
                // Capturamos la excepción "duplicate column name" y la ignoramos,
                // ya que significa que la columna ya existe.
                if (!alterEx.getMessage().contains("duplicate column name")) {
                    throw alterEx; // Relanzar si es otro error SQL grave
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}