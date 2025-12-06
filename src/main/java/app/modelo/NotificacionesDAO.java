package app.modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesDAO {

    // INSERTAR NUEVO DEUDOR
    public void VerificarNotificacionesAutomaticas() throws SQLException {
        String sqlEliminar = "DELETE FROM notificaciones WHERE revisada = 1";

        try (Connection conn = ConexionSQLiteNotificaciones.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlEliminar);
        }

        String sqlProductos = """
                SELECT id, nombre,cantidad
                FROM productos
                WHERE cantidad < 5
                """;
        
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlProductos)) {

            while (rs.next()) {
                int productoId = rs.getInt("id");
                //String nombre = rs.getString("nombre");
                //int cantidad = rs.getInt("cantidad");

                if (!existeNotificacionPendiente(productoId)) {
                    Notificaciones notificaciones = new Notificaciones();
                    notificaciones.setProductoId(productoId);
                    notificaciones.setProductoNombre(rs.getString("nombre"));
                    notificaciones.setCantidadActual(rs.getInt("cantidad"));
                    notificaciones.setCantidadMinima(5);

                    agregar(notificaciones);
                }
            }
        }
    }

    public void agregar(Notificaciones n) throws SQLException {
        String sql = """
            INSERT INTO notificaciones
            (producto_id, producto_nombre, cantidad_actual, cantidad_minima, fecha_creacion, revisada)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 0)
        """;

        try (Connection conn = ConexionSQLiteNotificaciones.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, n.getProductoId());
            pstmt.setString(2, n.getProductoNombre());
            pstmt.setInt(3, n.getCantidadActual());
            pstmt.setInt(4, n.getCantidadMinima());

            pstmt.executeUpdate();
        }
    }

    public boolean existeNotificacionPendiente(int productoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notificaciones WHERE producto_id = ? AND revisada = 0";

        try (Connection conn = ConexionSQLiteNotificaciones.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
                pstmt.setInt(1, productoId);
                ResultSet rs = pstmt.executeQuery();

                return rs.getInt(1) > 0;
            }            
    }

    public List<Notificaciones> listarTodas() throws SQLException {
        List<Notificaciones> lista = new ArrayList<>();

        String sql = "SELECT * FROM notificaciones WHERE revisada = 0 ORDER BY fecha_creacion DESC";

        try (Connection conn = ConexionSQLiteNotificaciones.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void marcarRevisada(int id) throws SQLException {
        String sql = "DELETE FROM notificaciones WHERE id = ?";

        try (Connection conn = ConexionSQLiteNotificaciones.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void actualizarCantidadActual(int productoId, int nuevaCantidad) throws SQLException {
        String sql = "UPDATE notificaciones SET cantidad_actual = ? WHERE producto_id = ? AND revisada = 0";

        try (Connection conn = ConexionSQLiteNotificaciones.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, nuevaCantidad);
                pstmt.setInt(2, productoId);
                pstmt.executeUpdate();
            }
    }

    public void eliminarNotificacion(int id) throws SQLException {
        String sql = "DELETE FROM notificaciones WHERE id = ?";

        try (Connection conn = ConexionSQLiteNotificaciones.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
    }

    private Notificaciones mapear(ResultSet rs) throws SQLException {
        Notificaciones n = new Notificaciones();
        n.setId(rs.getInt("id"));
        n.setProductoId(rs.getInt("producto_id"));
        n.setProductoNombre(rs.getString("producto_nombre"));
        n.setCantidadActual(rs.getInt("cantidad_actual"));
        n.setCantidadMinima(rs.getInt("cantidad_minima"));
        n.setFechaCreacion(rs.getDate("fecha_creacion"));
        n.setRevisada(rs.getBoolean("revisada"));
        return n;
    }
}

