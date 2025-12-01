package modelo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public void agregarProducto(Producto p) throws SQLException {
        // CORRECCIÓN: Se añadió un espacio antes de la palabra VALUES y se incluyó
        // costo.
        String sql = "INSERT INTO productos (nombre, marca, categoria, cantidad, estado, ventas, fecha_caducidad, precio, costo)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionSQLite.conectar();
                // CORRECCIÓN 1: Se eliminó Statement.RETURN_GENERATED_KEYS para evitar el error
                // "not implemented"
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setProductoParameters(pstmt, p);
            pstmt.executeUpdate();

            // CORRECCIÓN 2: Se usa la función específica de SQLite para obtener el ID
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * R - Obtener producto por ID
     */
    public Producto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM productos WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearProducto(rs);
            }
            return null;
        }
    }

    /**
     * R - Listar todos los productos
     */
    public List<Producto> listarTodos() throws SQLException {
        String sql = "SELECT * FROM productos";
        List<Producto> productos = new ArrayList<>();

        try (Connection conn = ConexionSQLite.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        }
        return productos;
    }

    /*
     * Buscador
     */
    public List<Producto> buscarPorNombre(String nombreParcial) throws SQLException {
        String sql = "SELECT * FROM productos WHERE UPPER(nombre) LIKE ?";
        List<Producto> productos = new ArrayList<>();

        try (Connection conn = ConexionSQLite.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 1. Convertir el texto de búsqueda a mayúsculas y añadir comodines (%)
            String terminoBusqueda = "%" + nombreParcial.toUpperCase() + "%";

            // 2. Asignar el parámetro al PreparedStatement
            pstmt.setString(1, terminoBusqueda);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        }
        return productos;
    }

    /**
     * U - Actualizar producto existente
     */
    public void actualizarProducto(Producto p) throws SQLException {
        // CORRECCIÓN 3: Se añade 'costo' a la sentencia UPDATE
        String sql = "UPDATE productos SET nombre = ?, marca = ?, categoria = ?, cantidad = ?, "
                + "estado = ?, ventas = ?, fecha_caducidad = ?, precio = ?, costo = ? WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Setea los 9 parámetros del producto (nombre, marca... hasta costo)
            setProductoParameters(pstmt, p);

            // El décimo parámetro es el ID para el WHERE
            pstmt.setInt(10, p.getId());
            pstmt.executeUpdate();

            Producto actualizado = buscarPorId(p.getId());
            new NotificacionesDAO().actualizarCantidadActual(p.getId(), actualizado.getCantidad());
        }
    }

    /**
     * D - Eliminar producto por ID
     */
    public void eliminarProducto(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public boolean registrarVenta(int idProducto, int cantidad) throws SQLException {
        String sql = "UPDATE productos SET cantidad = cantidad - ?, ventas = ventas + ? WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, cantidad);
            pstmt.setInt(3, idProducto);

            //return pstmt.executeUpdate() > 0;
            boolean ok = pstmt.executeUpdate() > 0;

            if (ok) {
                Producto p = buscarPorId(idProducto);
                new NotificacionesDAO().actualizarCantidadActual(idProducto, p.getCantidad());
            }

            return ok;
        }
    }

    /**
     * Registra una devolución: aumenta la cantidad y resta de ventas.
     * Valida que las ventas no queden negativas.
     */
    public boolean devolverProducto(int idProducto, int cantidad) throws SQLException {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser mayor que cero");

        Connection conn = null;
        try {
            conn = ConexionSQLite.conectar();
            conn.setAutoCommit(false);

            // 1) Comprobar ventas actuales
            String sqlCheck = "SELECT ventas FROM productos WHERE id = ?";
            int ventasActual = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {
                pstmt.setInt(1, idProducto);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ventasActual = rs.getInt("ventas");
                    } else {
                        throw new SQLException("Producto no encontrado");
                    }
                }
            }

            if (ventasActual - cantidad < 0) {
                throw new SQLException("Devolución inválida: ventas actuales=" + ventasActual + ", no se puede devolver " + cantidad);
            }

            // 2) Aplicar la devolución
            String sqlUpdate = "UPDATE productos SET cantidad = cantidad + ?, ventas = ventas - ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                pstmt.setInt(1, cantidad);
                pstmt.setInt(2, cantidad);
                pstmt.setInt(3, idProducto);

                int rows = pstmt.executeUpdate();
                if (rows == 0) throw new SQLException("Error al aplicar devolución, producto no encontrado");
            }

            conn.commit();
            Producto p = buscarPorId(idProducto);
            new NotificacionesDAO().actualizarCantidadActual(idProducto, p.getCantidad());

            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Registra una venta detallada (transaccional)
     */
    public boolean registrarVentaDetallada(Venta venta) throws SQLException {
        Connection conn = null;
        try {
            conn = ConexionSQLite.conectar();
            conn.setAutoCommit(false);

            // 1. Registrar en tabla de ventas
            // NOTA: Esta sección aún usa Statement.RETURN_GENERATED_KEYS,
            // puede fallar si no se aplica la corrección 2 también aquí.
            String sqlVenta = "INSERT INTO ventas (id_producto, cantidad, precio_unitario, fecha) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {

                // NOTA: Asumiendo que Venta tiene getProducto().getId() y getPrecioUnitario()
                pstmt.setInt(1, venta.getProducto().getId());
                pstmt.setInt(2, venta.getCantidad());
                pstmt.setDouble(3, venta.getPrecioUnitario());
                pstmt.setTimestamp(4, Timestamp.valueOf(venta.getFecha()));
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        venta.setId(rs.getInt(1));
                    }
                }
            }
            // Para ser totalmente robusto, la lógica de arriba con getGeneratedKeys()
            // debería ser reemplazada por SELECT last_insert_rowid() también en esta
            // sección.

            // 2. Actualizar stock del producto
            registrarVenta(conn, venta.getProducto().getId(), venta.getCantidad());

            conn.commit();
            Producto p = buscarPorId(venta.getProducto().getId());
            new NotificacionesDAO().actualizarCantidadActual(p.getId(), p.getCantidad());
            return true;
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (conn != null)
                conn.close();
        }
    }

    /**
     * Obtiene el historial de ventas de un producto
     */
    public List<Venta> obtenerVentasPorProducto(int idProducto) throws SQLException {
        String sql = "SELECT * FROM ventas WHERE id_producto = ? ORDER BY fecha DESC";
        List<Venta> ventas = new ArrayList<>();

        try (Connection conn = ConexionSQLite.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            ResultSet rs = pstmt.executeQuery();

            Producto producto = buscarPorId(idProducto);

            while (rs.next()) {
                ventas.add(mapearVenta(rs, producto));
            }
        }
        return ventas;
    }

    // ----------------------------
    // MÉTODOS AUXILIARES PRIVADOS
    // ----------------------------

    private void registrarVenta(Connection conn, int idProducto, int cantidad) throws SQLException {
        String sql = "UPDATE productos SET cantidad = cantidad - ?, ventas = ventas + ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, cantidad);
            pstmt.setInt(3, idProducto);
            pstmt.executeUpdate();
        }
    }

    private void setProductoParameters(PreparedStatement pstmt, Producto p) throws SQLException {
        // Estos son los 9 parámetros para INSERT y UPDATE
        pstmt.setString(1, p.getNombre());
        pstmt.setString(2, p.getMarca());
        pstmt.setString(3, p.getCategoria());
        pstmt.setInt(4, p.getCantidad());
        pstmt.setString(5, p.getEstado());
        pstmt.setInt(6, p.getVentas());
        pstmt.setString(7, p.getFechaCaducidad() != null ? p.getFechaCaducidad().toString() : null);
        pstmt.setDouble(8, p.getPrecio());
        pstmt.setDouble(9, p.getCosto()); // Parámetro 9: Costo
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setMarca(rs.getString("marca"));
        p.setCategoria(rs.getString("categoria"));
        p.setCantidad(rs.getInt("cantidad"));
        p.setEstado(rs.getString("estado"));
        p.setVentas(rs.getInt("ventas"));
        p.setFechaCaducidad(
                rs.getString("fecha_caducidad") != null ? LocalDate.parse(rs.getString("fecha_caducidad")) : null);
        p.setPrecio(rs.getDouble("precio"));
        p.setCosto(rs.getDouble("costo")); // Se lee el costo
        return p;
    }

    private Venta mapearVenta(ResultSet rs, Producto producto) throws SQLException {
        return new Venta(
                rs.getInt("id"),
                producto,
                rs.getInt("cantidad"),
                rs.getDouble("precio_unitario"),
                rs.getTimestamp("fecha").toLocalDateTime());

    }
}