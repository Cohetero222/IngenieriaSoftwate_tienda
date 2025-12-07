package app.pruebas;

import app.modelo.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PruebaSistemaVisual {

    public static void main(String[] args) {
        try {
            System.out.println("==== INICIANDO PRUEBA DEL SISTEMA ====\n");

            // -----------------------------
            // Inicializar BD de Notificaciones
            // -----------------------------
            ConexionSQLiteNotificaciones.inicializarBD();

            // -----------------------------
            // Crear Productos de prueba
            // -----------------------------
            ProductoDAO productoDAO = new ProductoDAO();
            Producto p1 = new Producto("Leche", "MarcaA", "Lácteos", 10, "Disponible", 0,
                    LocalDate.now().plusDays(10), 20.0, 15.0);
            Producto p2 = new Producto("Pan", "MarcaB", "Panadería", 2, "Disponible", 0,
                    LocalDate.now().plusDays(2), 10.0, 5.0);

            productoDAO.agregarProducto(p1);
            productoDAO.agregarProducto(p2);

            System.out.println("=== Productos agregados ===");
            mostrarProductos(productoDAO.listarTodos());

            // -----------------------------
            // Registrar una Venta
            // -----------------------------
            Venta venta = new Venta();
            DetalleVenta dv1 = new DetalleVenta(p1, 3, p1.getPrecio());
            DetalleVenta dv2 = new DetalleVenta(p2, 1, p2.getPrecio());
            venta.AgregarDetalle(dv1);
            venta.AgregarDetalle(dv2);

            productoDAO.registrarVentaDetallada(venta);

            System.out.println("\n=== Venta registrada ===");
            System.out.println(venta);

            System.out.println("\n=== Stock después de la venta ===");
            mostrarProductos(productoDAO.listarTodos());

            // -----------------------------
            // Devolver un producto
            // -----------------------------
            boolean devuelto = productoDAO.devolverProducto(p1.getId(), 2);
            System.out.println("\n=== Devolución de 2 unidades de Leche ===");
            System.out.println(devuelto ? "Devolución exitosa" : "Devolución fallida");

            System.out.println("\n=== Stock después de la devolución ===");
            mostrarProductos(productoDAO.listarTodos());

            // -----------------------------
            // Verificar Notificaciones Automáticas
            // -----------------------------
            NotificacionesDAO notiDAO = new NotificacionesDAO();
            notiDAO.VerificarNotificacionesAutomaticas();
            List<Notificaciones> notificaciones = notiDAO.listarTodas();

            System.out.println("\n=== Notificaciones pendientes ===");
            mostrarNotificaciones(notificaciones);

            // Marcar la primera notificación como revisada
            if (!notificaciones.isEmpty()) {
                Notificaciones primera = notificaciones.get(0);
                notiDAO.marcarRevisada(primera.getId());
                System.out.println("\nSe marcó como revisada la notificación de: " + primera.getProductoNombre());
            }

            System.out.println("\n=== Notificaciones actuales ===");
            mostrarNotificaciones(notiDAO.listarTodas());

            // -----------------------------
            // Registrar Deudores
            // -----------------------------
            DeudoresDAO deudoresDAO = new DeudoresDAO();
            Deudores d1 = new Deudores("Juan Perez", "Leche", "MarcaA", "Lácteos", 2, 20.0, 15.0);
            deudoresDAO.agregarDeudor(d1);

            System.out.println("\n=== Deudores registrados ===");
            mostrarDeudores(deudoresDAO.listarTodos());

            // -----------------------------
            // Consultar ventas por fecha
            // -----------------------------
            VentaDAO ventaDAO = new VentaDAO();
            Date inicio = Date.valueOf(LocalDate.now().minusDays(1));
            Date fin = Date.valueOf(LocalDate.now().plusDays(1));

            List<Venta> ventasHoy = VentaDAO.obtenerVentasPorFecha(inicio, fin);

            System.out.println("\n=== Ventas registradas hoy ===");
            for (Venta v : ventasHoy) {
                System.out.println(v);
            }

            System.out.println("\n==== PRUEBA DEL SISTEMA COMPLETA ====");

        } catch (SQLException e) {
            System.err.println("Error en prueba visual: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -----------------------------
    // MÉTODOS AUXILIARES PARA IMPRESIÓN
    // -----------------------------
    private static void mostrarProductos(List<Producto> productos) {
        for (Producto p : productos) {
            System.out.printf("ID: %d | Nombre: %s | Marca: %s | Cantidad: %d | Estado: %s | Ventas: %d%n",
                    p.getId(), p.getNombre(), p.getMarca(), p.getCantidad(), p.getEstado(), p.getVentas());
        }
    }

    private static void mostrarNotificaciones(List<Notificaciones> notificaciones) {
        if (notificaciones.isEmpty()) {
            System.out.println("No hay notificaciones pendientes.");
        } else {
            for (Notificaciones n : notificaciones) {
                System.out.printf("ID: %d | Producto: %s | Cantidad Actual: %d | Minima: %d | Fecha: %s%n",
                        n.getId(), n.getProductoNombre(), n.getCantidadActual(), n.getCantidadMinima(), n.getFechaCreacion());
            }
        }
    }

    private static void mostrarDeudores(List<Deudores> deudores) {
        if (deudores.isEmpty()) {
            System.out.println("No hay deudores registrados.");
        } else {
            for (Deudores d : deudores) {
                System.out.printf("ID: %d | Nombre: %s | Producto: %s | Marca: %s | Cantidad: %d | Precio: %.2f%n",
                        d.getId(), d.getNombre(), d.getProducto(), d.getMarca(), d.getCantidad(), d.getPrecio());
            }
        }
    }
}
