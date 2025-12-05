package app.modelo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    /* Obtener ventas por fecha */
    private static final String SelectVentasPorFechaSQL = "SELECT v.id, v.cantidad, v.precio_unitario, v.fecha, "
            + "p.id AS id_producto, p.nombre, p.categoria " +
            "FROM ventas v " + "JOIN productos p ON v.id_producto = p.id "
            + "WHERE datetime(v.fecha) BETWEEN datetime(?) AND datetime(?);";

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
                List<DetalleVenta> Detalles = venta.getVentaDetalles();
                for(DetalleVenta V : Detalles){
                    pstmtVenta.setInt(1, V.getProducto().getId());
                    pstmtVenta.setInt(2, V.getCantidad());
                    pstmtVenta.setDouble(3, V.getPrecioUnitario());
                    pstmtVenta.setString(4, venta.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    pstmtVenta.executeUpdate();
                }
                
            }

            // B. Actualizar el stock en la tabla 'productos'
            try (PreparedStatement pstmtStock = conn.prepareStatement(UPDATE_STOCK_SQL)) {
                List<DetalleVenta> Detalles = venta.getVentaDetalles();
                for(DetalleVenta V : Detalles){
                    pstmtStock.setInt(1, V.getCantidad()); // Cantidad a restar del stock
                    pstmtStock.setInt(2, V.getCantidad()); // Cantidad a sumar a las ventas
                    pstmtStock.setInt(3, V.getProducto().getId()); // ID del producto

                    int filasAfectadas = pstmtStock.executeUpdate();
                    if (filasAfectadas == 0) {
                        // Lanza una excepción si el producto no existe o algo salió mal
                        throw new SQLException("Error al actualizar stock, producto no encontrado o stock insuficiente.");
                    }
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

    /* Metodo para filtrar las ventas (mes pasado) */
    public static List<Venta> obtenerVentasPorFecha(Date fechaInicio, Date fechaFin) throws SQLException {
        List<Venta> ventas = new ArrayList<>();

        try (Connection conn = ConexionSQLite.conectar();
                PreparedStatement pstmt = conn.prepareStatement(SelectVentasPorFechaSQL)) {

            String inicioStr = fechaInicio.toString() + " 00:00:00";
            String finStr = fechaFin.toString() + " 23:59:59";

            pstmt.setString(1, inicioStr);
            pstmt.setString(2, finStr);

            /* Recorremos las consultas */
            try (ResultSet rs = pstmt.executeQuery()) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                while (rs.next()) {
                    /* Creamos los productos y ventas de la lista filtrada */
                    Producto producto = new Producto();
                    producto.setId(rs.getInt("id_producto"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setCategoria(rs.getString("categoria"));

                    Venta venta = new Venta();
                    DetalleVenta DV = new DetalleVenta();
                    venta.setId(rs.getInt("id"));
                    DV.setProducto(producto);
                    DV.setCantidad(rs.getInt("cantidad"));
                    DV.setPrecioUnitario(rs.getDouble("precio_unitario"));

                    // Leer la fecha como String y convertir a LocalDateTime
                    String fechaStr = rs.getString("fecha");
                    if (fechaStr != null && !fechaStr.isBlank()) {
                        venta.setFecha(LocalDateTime.parse(fechaStr, fmt));
                    }
                    venta.AgregarDetalle(DV);
                    ventas.add(venta);
                }
            }
        }

        return ventas;
    }
}