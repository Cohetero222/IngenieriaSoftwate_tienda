package vista;

import modelo.Producto;
import modelo.ProductoDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Comparator; // Necesario para ordenar si se desea

public class DialogoReportes extends JDialog {

    public DialogoReportes(JFrame parent) {
        // Llama al constructor de JDialog: padre, título, modal (true)
        super(parent, "Reportes", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);

        JTabbedPane pestanas = new JTabbedPane();

        // 1️⃣ Pestaña: Productos más vendidos (gráfico de barras)
        // Se crea un JPanel anónimo donde se sobrescribe paintComponent para dibujar el
        // gráfico.
        JPanel panelVendidos = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ProductoDAO dao = new ProductoDAO();
                    // Se obtienen los datos de la base de datos
                    List<Producto> productos = dao.listarTodos();

                    if (productos.isEmpty()) {
                        g.drawString("No hay datos para mostrar", 50, 50);
                        return;
                    }

                    // Opcional: limitar el número de barras si la lista es muy grande
                    // productos =
                    // productos.stream().sorted(Comparator.comparingInt(Producto::getVentas).reversed()).limit(10).toList();

                    // Obtiene el máximo de ventas para escalar el gráfico
                    int maxVentas = productos.stream()
                            .mapToInt(Producto::getVentas)
                            .max().orElse(1);

                    int x = 50;
                    int barWidth = 50;
                    // base Y se define cerca de la parte inferior del panel
                    int baseY = getHeight() - 50;

                    // Se dibuja un eje X
                    g.setColor(Color.BLACK);
                    g.drawLine(x - 10, baseY, getWidth() - 50, baseY);

                    for (Producto p : productos) {
                        // Calcula la altura de la barra, escalando hasta un máximo de 300px
                        int altura = (int) ((p.getVentas() / (double) maxVentas) * 300);

                        // Dibuja el relleno de la barra
                        g.setColor(Color.BLUE);
                        g.fillRect(x, baseY - altura, barWidth, altura);

                        // Dibuja el contorno
                        g.setColor(Color.BLACK);
                        g.drawRect(x, baseY - altura, barWidth, altura);

                        // Dibuja el nombre del producto
                        g.drawString(p.getNombre(), x, baseY + 20);

                        // Mueve la posición X para la siguiente barra
                        x += barWidth + 20;
                    }
                } catch (SQLException e) {
                    g.drawString("Error al cargar datos: " + e.getMessage(), 50, 50);
                }
            }
        }; // Fin del panelVendidos

        pestanas.addTab("Más Vendidos", panelVendidos);

        // 2️⃣ Pestaña: Estado de productos (gráfico de pastel) - Lógica ya era
        // funcional
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

                        // Dibuja el segmento del pastel
                        g.setColor(colores[i]);
                        g.fillArc(200, 150, 300, 300, startAngle, angle);
                        startAngle += angle;

                        // Dibuja la leyenda
                        g.setColor(Color.BLACK);
                        g.drawString(etiquetas[i] + ": " + valores[i], 550, 200 + i * 20);
                    }

                } catch (SQLException e) {
                    g.drawString("Error al cargar datos: " + e.getMessage(), 50, 50);
                }
            }
        }; // Fin del panelEstados

        pestanas.addTab("Estados", panelEstados);

        add(pestanas);
    }
}