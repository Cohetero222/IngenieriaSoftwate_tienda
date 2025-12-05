package app.vista;

import app.modelo.DetalleVenta;
import app.modelo.Producto;
import app.modelo.ProductoDAO;
import app.modelo.Venta;
import app.modelo.VentaDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public class DialogoReportes extends JDialog {

    public DialogoReportes(JFrame parent) {

        super(parent, "Reportes", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);

        JTabbedPane pestanas = new JTabbedPane();

        /* ------------------------------------------------------------
            1) GRÁFICO DE BARRAS – Productos más vendidos
        ------------------------------------------------------------- */
        JPanel panelVendidos = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ProductoDAO dao = new ProductoDAO();
                    List<Producto> productos = dao.listarTodos();

                    if (productos.isEmpty()) {
                        g.drawString("No hay datos para mostrar", 50, 50);
                        return;
                    }

                    int maxVentas = productos.stream()
                            .mapToInt(Producto::getVentas)
                            .max().orElse(1);

                    int x = 50;
                    int barWidth = 50;
                    int baseY = getHeight() - 50;

                    g.setColor(Color.BLACK);
                    g.drawLine(x - 10, baseY, getWidth() - 50, baseY);

                    for (Producto p : productos) {
                        int altura = (int) ((p.getVentas() / (double) maxVentas) * 300);

                        g.setColor(Color.BLUE);
                        g.fillRect(x, baseY - altura, barWidth, altura);

                        g.setColor(Color.BLACK);
                        g.drawRect(x, baseY - altura, barWidth, altura);
                        g.drawString(p.getNombre(), x, baseY + 20);

                        x += barWidth + 20;
                    }

                } catch (SQLException e) {
                    g.drawString("Error al cargar datos: " + e.getMessage(), 50, 50);
                }
            }
        };

        pestanas.addTab("Más Vendidos", panelVendidos);


        /* ------------------------------------------------------------
            2) GRÁFICO DE PASTEL – Estado de productos
        ------------------------------------------------------------- */
        JPanel panelEstados = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                try {
                    ProductoDAO dao = new ProductoDAO();
                    List<Producto> productos = dao.listarTodos();

                    int disponibles = 0, agotados = 0, caducados = 0, descontinuados = 0;

                    for (Producto p : productos) {
                        switch (p.getEstado()) {
                            case "Disponible" -> disponibles++;
                            case "Agotado" -> agotados++;
                            case "Caducado" -> caducados++;
                            case "Descontinuado" -> descontinuados++;
                        }
                    }

                    int total = disponibles + agotados + caducados + descontinuados;
                    if (total == 0) {
                        g.drawString("No hay datos disponibles", 50, 50);
                        return;
                    }

                    int startAngle = 0;
                    int[] valores = { disponibles, agotados, caducados, descontinuados };
                    Color[] colores = { Color.GREEN, Color.RED, Color.ORANGE, Color.GRAY };
                    String[] etiquetas = { "Disponible", "Agotado", "Caducado", "Descontinuado" };

                    for (int i = 0; i < valores.length; i++) {
                        int angle = (int) Math.round(360.0 * valores[i] / total);

                        g.setColor(colores[i]);
                        g.fillArc(200, 150, 300, 300, startAngle, angle);
                        startAngle += angle;

                        g.setColor(Color.BLACK);
                        g.drawString(etiquetas[i] + ": " + valores[i], 550, 200 + i * 20);
                    }

                } catch (SQLException e) {
                    g.drawString("Error al cargar datos: " + e.getMessage(), 50, 50);
                }
            }
        };

        pestanas.addTab("Estados", panelEstados);


        /* ------------------------------------------------------------
            3) TABLAS DE REPORTES
        ------------------------------------------------------------- */
        JPanel PanelTablas = new JPanel();
        PanelTablas.setLayout(new BoxLayout(PanelTablas, BoxLayout.Y_AXIS));
        PanelTablas.setBackground(Color.WHITE);

        /* TABLA – MÁS VENDIDOS */
        JPanel SeccionMasVendidos = crearPanelTabla("Productos Más Vendidos");
        JTable tablaMasVendidos = crearTabla(5, new String[]{"Nombre", "Cantidad Vendida"});
        SeccionMasVendidos.add(new JScrollPane(tablaMasVendidos), BorderLayout.CENTER);

        /* TABLA – MAYOR GANANCIA */
        JPanel SeccionMayorGanancia = crearPanelTabla("Productos con Mayor Ganancia");
        JTable tablaMayorGanancia = crearTabla(5, new String[]{"Nombre", "Ganancia"});
        SeccionMayorGanancia.add(new JScrollPane(tablaMayorGanancia), BorderLayout.CENTER);

        /* TABLA – MENOS VENDIDOS */
        JPanel SeccionMenosVendidos = crearPanelTabla("Productos Menos Vendidos");
        JTable tablaMenosVendidos = crearTabla(5, new String[]{"Nombre", "Cantidad Vendida"});
        SeccionMenosVendidos.add(new JScrollPane(tablaMenosVendidos), BorderLayout.CENTER);

        PanelTablas.add(SeccionMasVendidos);
        PanelTablas.add(Box.createVerticalStrut(20));
        PanelTablas.add(SeccionMayorGanancia);
        PanelTablas.add(Box.createVerticalStrut(20));
        PanelTablas.add(SeccionMenosVendidos);


        /* ------------------------------------------------------------
            CÁLCULO Y LLENADO DE TABLAS
        ------------------------------------------------------------- */

        try {
            LocalDate inicioMes = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

            Date inicioSQL = Date.valueOf(inicioMes);
            Date finSQL = Date.valueOf(finMes);

            JLabel EtiquetaMes = new JLabel("Reporte del mes: " +
                    inicioMes.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
            EtiquetaMes.setFont(new Font("Arial", Font.BOLD, 16));
            PanelTablas.add(EtiquetaMes, 0);

            List<Venta> ventasMes = VentaDAO.obtenerVentasPorFecha(inicioSQL, finSQL);

            if (ventasMes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay ventas en el mes pasado.");
            } else {

                Map<Integer, Integer> CantVendidas = new HashMap<>();
                Map<Integer, Double> Ganancias = new HashMap<>();
                Map<Integer, Producto> Productos = new HashMap<>();

                for (Venta v : ventasMes) {
                    for (DetalleVenta dv : v.getVentaDetalles()) {
                        int id = dv.getProducto().getId();
                        Productos.put(id, dv.getProducto());

                        CantVendidas.put(id, CantVendidas.getOrDefault(id, 0) + dv.getCantidad());

                        double ganancia = dv.getCantidad() * dv.getPrecioUnitario();
                        Ganancias.put(id, Ganancias.getOrDefault(id, 0.0) + ganancia);
                    }
                }

                List<Integer> MasVendidos = CantVendidas.keySet().stream()
                        .sorted((a, b) -> CantVendidas.get(b) - CantVendidas.get(a))
                        .limit(5).toList();

                List<Integer> MenosVendidos = CantVendidas.keySet().stream()
                        .sorted(Comparator.comparingInt(CantVendidas::get))
                        .limit(5).toList();

                List<Integer> MayorGanancia = Ganancias.keySet().stream()
                        .sorted((a, b) -> Double.compare(Ganancias.get(b), Ganancias.get(a)))
                        .limit(5).toList();

                llenar(tablaMasVendidos, MasVendidos, CantVendidas, Productos);
                llenar(tablaMenosVendidos, MenosVendidos, CantVendidas, Productos);
                llenarGanancias(tablaMayorGanancia, MayorGanancia, Ganancias, Productos);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        pestanas.addTab("Tablas", PanelTablas);

        add(pestanas);
    }

    /* ------------------------------------------------------------
            MÉTODOS AUXILIARES
    ------------------------------------------------------------- */
    private JPanel crearPanelTabla(String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        return p;
    }

    private JTable crearTabla(int filas, String[] columnas) {
        JTable t = new JTable(new Object[filas][columnas.length], columnas);
        t.setRowHeight(25);
        t.setFont(new Font("Arial", Font.PLAIN, 14));
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        return t;
    }

    private void llenar(JTable tabla, List<Integer> lista, Map<Integer, Integer> cant,
                        Map<Integer, Producto> productos) {

        for (int i = 0; i < lista.size(); i++) {
            int id = lista.get(i);
            tabla.setValueAt(productos.get(id).getNombre(), i, 0);
            tabla.setValueAt(cant.get(id), i, 1);
        }
    }

    private void llenarGanancias(JTable tabla, List<Integer> lista, Map<Integer, Double> gan,
                                 Map<Integer, Producto> productos) {

        for (int i = 0; i < lista.size(); i++) {
            int id = lista.get(i);
            tabla.setValueAt(productos.get(id).getNombre(), i, 0);
            tabla.setValueAt(gan.get(id), i, 1);
        }
    }
}