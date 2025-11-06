package modelo;

import java.sql.*;
import java.time.format.DateTimeFormatter;

public class VentaDAO {
    private static final String INSERT_VENTA_SQL = "INSERT INTO ventas (id_producto, cantidad, precio_unitario, fecha) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_STOCK_SQL = "UPDATE productos SET cantidad = cantidad - ?, ventas = ventas + ? WHERE id = ?";

    public void guardarVenta(Venta venta) throws SQLException {
        Connection conn = null; // La conexión debe estar fuera del try-with-resources para el rollback
        try {
            conn = ConexionSQLite.conectar();
            // 1. Iniciar la transacción: desactivar el auto-commit
            conn.setAutoCommit(false);

            // A. Registrar la venta en la tabla 'ventas'
            try (PreparedStatement pstmtVenta = conn.prepareStatement(INSERT_VENTA_SQL)) {
                pstmtVenta.setInt(1, venta.getProducto().getId());
                pstmtVenta.setInt(2, venta.getCantidad());
                pstmtVenta.setDouble(3, venta.getPrecioUnitario());
                pstmtVenta.setString(4, venta.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                pstmtVenta.executeUpdate();
            }

            // B. Actualizar el stock en la tabla 'productos'
            try (PreparedStatement pstmtStock = conn.prepareStatement(UPDATE_STOCK_SQL)) {
                pstmtStock.setInt(1, venta.getCantidad()); // Cantidad a restar del stock
                pstmtStock.setInt(2, venta.getCantidad()); // Cantidad a sumar a las ventas
                pstmtStock.setInt(3, venta.getProducto().getId()); // ID del producto

                int filasAfectadas = pstmtStock.executeUpdate();
                if (filasAfectadas == 0) {
                    // Lanza una excepción si el producto no existe o algo salió mal
                    throw new SQLException("Error al actualizar stock, producto no encontrado o stock insuficiente.");
                }
            }

            // 2. Finalizar la transacción: Si todo fue bien, guardar los cambios
            conn.commit();

        } catch (SQLException e) {
            // 3. Rollback: Si algo falló, deshacer todos los cambios
            if (conn != null) {
                conn.rollback();
            }
            // Relanzar la excepción para informar al FormularioVenta
            throw e;
        } finally {
            // 4. Cerrar recursos y restaurar auto-commit
            if (conn != null) {
                conn.setAutoCommit(true); // Restaurar el comportamiento normal
                conn.close();
            }
        }
    }
}