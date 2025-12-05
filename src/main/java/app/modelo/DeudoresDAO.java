package app.modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeudoresDAO {

    // INSERTAR NUEVO DEUDOR
    public void agregarDeudor(Deudores d) throws SQLException {
        String sql = """
                    INSERT INTO deudores
                    (nombre, producto, marca, categoria, cantidad, precio, costo)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConexionSQLiteDevolver.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setDeudorParameters(pstmt, d);
            pstmt.executeUpdate();

            // Obtener ID generado
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    d.setId(rs.getInt(1));
                }
            }
        }
    }

    // BUSCAR POR ID
    public Deudores buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM deudores WHERE id = ?";

        try (Connection conn = ConexionSQLiteDevolver.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearDeudor(rs);
            }
            return null;
        }
    }

    // LISTAR TODOS LOS DEUDORES
    public List<Deudores> listarTodos() throws SQLException {
        List<Deudores> lista = new ArrayList<>();
        String sql = "SELECT * FROM deudores";

        try (Connection conn = ConexionSQLiteDevolver.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearDeudor(rs));
            }
        }
        return lista;
    }

    // ACTUALIZAR REGISTRO
    public void actualizarDeudor(Deudores d) throws SQLException {
        String sql = """
                    UPDATE deudores SET
                    nombre = ?, producto = ?, marca = ?, categoria = ?,
                    cantidad = ?, precio = ?, costo = ?
                    WHERE id = ?
                """;

        try (Connection conn = ConexionSQLiteDevolver.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setDeudorParameters(pstmt, d);
            pstmt.setInt(8, d.getId());
            pstmt.executeUpdate();
        }
    }

    // ELIMINAR POR ID
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM deudores WHERE id = ?";

        try (Connection conn = ConexionSQLiteDevolver.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // ------- MÉTODOS AUXILIARES ---------

    private void setDeudorParameters(PreparedStatement pstmt, Deudores d) throws SQLException {
        pstmt.setString(1, d.getNombre());
        pstmt.setString(2, d.getProducto());
        pstmt.setString(3, d.getMarca());
        pstmt.setString(4, d.getCategoria());
        pstmt.setInt(5, d.getCantidad());
        pstmt.setDouble(6, d.getPrecio());
        pstmt.setDouble(7, d.getCosto());
    }

    private Deudores mapearDeudor(ResultSet rs) throws SQLException {
        Deudores d = new Deudores();
        d.setId(rs.getInt("id"));
        d.setNombre(rs.getString("nombre"));
        d.setProducto(rs.getString("producto"));
        d.setMarca(rs.getString("marca"));
        d.setCategoria(rs.getString("categoria"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecio(rs.getDouble("precio"));
        d.setCosto(rs.getDouble("costo"));
        return d;
    }
}
