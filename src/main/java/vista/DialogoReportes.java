package vista;

import modelo.Producto;
import modelo.ProductoDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Comparator; // Necesario para ordenar si se desea
<<<<<<< HEAD
import java.util.HashMap;
import java.time.LocalDate; //Para el calculo de fechas
import java.time.format.TextStyle;
import java.sql.Date; //Para conversion a SQL Date
import java.util.Locale;
import java.util.Map;

import modelo.Venta;
import modelo.VentaDAO;

public class DialogoReportes extends JDialog {
    public DialogoReportes(JFrame parent) {

=======

public class DialogoReportes extends JDialog {

    public DialogoReportes(JFrame parent) {
>>>>>>> 9596c3dff79a57d762061ad282a51c7a71227398
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

<<<<<<< HEAD
                    // Limitar a los 10 productos mas venidos durante el mes
                    productos = productos.stream()
                            .sorted(Comparator.comparingInt(Producto::getVentas).reversed()).
                            limit(10).toList();
=======
                    // Opcional: limitar el número de barras si la lista es muy grande
                    // productos =
                    // productos.stream().sorted(Comparator.comparingInt(Producto::getVentas).reversed()).limit(10).toList();
>>>>>>> 9596c3dff79a57d762061ad282a51c7a71227398

                    // Obtiene el máximo de ventas para escalar el gráfico
                    int maxVentas = productos.stream()
                            .mapToInt(Producto::getVentas)
                            .max().orElse(1);

                    int x = 50;
                    int barWidth = 50;
                    // base Y se define cerca de la parte inferior del panel
                    int baseY = getHeight() - 50;

<<<<<<< HEAD
                    // Obtenemos el mes pasado
                    LocalDate fechaActual = LocalDate.now();
                    LocalDate mesPasadoDate = fechaActual.minusMonths(1);

                    // Formatear el mes en español
                    String mesPasado = mesPasadoDate.format(
                        java.time.format.DateTimeFormatter.ofPattern("MMMM", new Locale("es", "ES"))
                        );
                    
                    // Capitalizar la primera letra del mes
                    mesPasado = mesPasado.substring(0, 1).toUpperCase() + mesPasado.substring(1);


                    // **Dibuja el título del gráfico**
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.BOLD, 16));
                    String titulo = "Productos Más Vendidos - " + mesPasado;
                    int tituloWidth = g.getFontMetrics().stringWidth(titulo);
                    g.drawString(titulo, (getWidth() - tituloWidth) / 2, 30);

=======
>>>>>>> 9596c3dff79a57d762061ad282a51c7a71227398
                    // Se dibuja un eje X
                    g.setColor(Color.BLACK);
                    g.drawLine(x - 10, baseY, getWidth() - 50, baseY);

<<<<<<< HEAD
                    // Se dibuja el eje Y
                    g.drawLine(x - 10, baseY, getWidth() - 50, baseY);

                    // Dibujar marcas y valores en el eje Y
                    g.setFont(new Font("Arial", Font.PLAIN, 15));
                    for (int i = 0; i <= 5; i++){
                        int valor = (maxVentas * i) / 5;
                        int yPos = baseY - (300 * i) / 5;

                        // Dibuja la marca en el eje Y
                        g.drawLine(x - 15, yPos, x - 10, yPos);

                        // Dibuja el valor numérico
                        g.drawString(String.valueOf(valor), x - 40, yPos + 5);
                    }

                    // Etiqueta para el eje Y
                    g.setFont(new Font("Arial", Font.BOLD, 15));
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.rotate(-Math.PI / 2);
                    g2d.rotate(Math.PI / 2);

=======
>>>>>>> 9596c3dff79a57d762061ad282a51c7a71227398
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
<<<<<<< HEAD
                        g.setColor(Color.BLACK);
                        g.setFont(new Font("Arial", Font.PLAIN, 11));

                        String nombre = p.getNombre().trim();

                        // Estrategias para nombres largos:

                        if (nombre.length() > 12){
                            // Para nombres muy largos, dividir en dos líneas
                            int mid = nombre.length() / 2;
                            // Encontrar el mejor punto para dividir (espacio más cercano al medio)
                            int splitPoint = mid;

                            for (int i = 0; i < mid; i++){
                                if (mid + i < nombre.length() && nombre.charAt(mid + i) == ' '){
                                    splitPoint = mid + i;
                                    break;
                                }
                            }

                            String line1 = nombre.substring(0, splitPoint).trim();
                            String line2 = nombre.substring(splitPoint).trim();

                            // Centrar texto debajo de la barra
                            int textWidth1 = g.getFontMetrics().stringWidth(line1);
                            int textWidth2 = g.getFontMetrics().stringWidth(line2);
                    
                            g.drawString(line1, x + (barWidth - textWidth1) / 2, baseY + 15);
                            g.drawString(line2, x + (barWidth - textWidth2) / 2, baseY + 30);
                        } else if (nombre.length() > 8){
                            // Para nombres moderadamente largos
                            g.setFont(new Font("Arial", Font.PLAIN, 11));
                            int textWidth = g.getFontMetrics().stringWidth(nombre);
                            g.drawString(nombre, x + (barWidth - textWidth) / 2, baseY + 15);
                        } else {
                            // Para nombres cortos lo centramos normalmente
                            int textWidth = g.getFontMetrics().stringWidth(nombre);
                            g.drawString(nombre, x + (barWidth - textWidth) / 2, baseY + 15);
                        }
=======
                        g.drawString(p.getNombre(), x, baseY + 20);
>>>>>>> 9596c3dff79a57d762061ad282a51c7a71227398

                        // Mueve la posición X para la siguiente barra
                        x += barWidth + 20;
                    }
                } catch (SQLException e) {
                    g.drawString("Error al cargar datos: " + e.getMessage(), 50, 50);
                }
            }
        }; // Fin del panelVendidos

<<<<<<< HEAD
        pestanas.addTab("Grafica", panelVendidos);
=======
        pestanas.addTab("Más Vendidos", panelVendidos);
>>>>>>> 9596c3dff79a57d762061ad282a51c7a71227398

        // 2️⃣ Pestaña: Estado de productos (gráfico de pastel) - Lógica ya era
        // funcional
        JPanel panelEstados = new JPanel() {
<<<<<<< HEAD

            @Override

            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                /* Lógica del gráfico de pastel */
=======
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
>>>>>>> 9596c3dff79a57d762061ad282a51c7a71227398
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

<<<<<<< HEAD

        /* Panel de tablas */
        JPanel PanelTablas = new JPanel();
        PanelTablas.setLayout(new BoxLayout(PanelTablas, BoxLayout.Y_AXIS));
        PanelTablas.setBackground(Color.WHITE);

        /* Tabla de los productos mas vendidos */
        JPanel SeccionMasVendidos = new JPanel(new BorderLayout());
        SeccionMasVendidos.setBorder(BorderFactory.createTitledBorder("Productos mas Vendidos"));
        SeccionMasVendidos.setBackground(Color.WHITE);
        String[] columnas1 = { "Nombre", "Cantidad Vendida" };
        Object[][] filas1 = new Object[5][2];
        JTable tablaMasVendidos = new JTable(filas1, columnas1);
        /* Diseño */
        tablaMasVendidos.setRowHeight(25);
        tablaMasVendidos.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaMasVendidos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        SeccionMasVendidos.add(new JScrollPane(tablaMasVendidos), BorderLayout.CENTER);

        /* Tabla de los productos con mayor ganancia */
        JPanel SeccionMayorGanancia = new JPanel(new BorderLayout());
        SeccionMayorGanancia.setBorder(BorderFactory.createTitledBorder("Productos con Mayor Ganancia"));
        SeccionMayorGanancia.setBackground(Color.WHITE);
        String[] columnas2 = { "Nombre", "Ganancia" };
        Object[][] filas2 = new Object[5][2];
        /* Diseño */
        JTable tablaMayorGanancia = new JTable(filas2, columnas2);
        tablaMayorGanancia.setRowHeight(25);
        tablaMayorGanancia.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaMayorGanancia.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        SeccionMayorGanancia.add(new JScrollPane(tablaMayorGanancia), BorderLayout.CENTER);

        /* Tabla de los productos menos vendidos */
        JPanel SeccionMenosVendidos = new JPanel(new BorderLayout());
        SeccionMenosVendidos.setBorder(BorderFactory.createTitledBorder("Productos menos Vendidos"));
        SeccionMenosVendidos.setBackground(Color.WHITE);
        String[] columnas3 = { "Nombre", "Cantidad Vendida" };
        Object[][] filas3 = new Object[5][2];
        JTable tablaMenosVendidos = new JTable(filas3, columnas3);
        /* Diseño */
        tablaMenosVendidos.setRowHeight(25);
        tablaMenosVendidos.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaMenosVendidos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        SeccionMenosVendidos.add(new JScrollPane(tablaMenosVendidos), BorderLayout.CENTER);

        /* Añadir las secciones */
        PanelTablas.add(SeccionMasVendidos);
        PanelTablas.add(Box.createVerticalStrut(20));
        PanelTablas.add(SeccionMayorGanancia);
        PanelTablas.add(Box.createVerticalStrut(20));
        PanelTablas.add(SeccionMenosVendidos);
        
        /*Llenado de las tablas*/
        try {

            /*Calculo para el mes pasado */
         
            LocalDate InicioMesPasado = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDate FinMesPasado = InicioMesPasado.withDayOfMonth(InicioMesPasado.lengthOfMonth());

            Date FechaInicioSQL = Date.valueOf(InicioMesPasado);
            Date FechaFinSQL = Date.valueOf(FinMesPasado);

            String Mes = InicioMesPasado.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            JLabel EtiquetaMes = new JLabel("Reporte de: " + Mes);
            EtiquetaMes.setFont(new Font("Arial", Font.BOLD, 16));
            PanelTablas.add(EtiquetaMes, 0);

            //VentaDAO vdao = new VentaDAO();
            List<Venta> ventasMes = VentaDAO.obtenerVentasPorFecha(FechaInicioSQL, FechaFinSQL);

            /*Si no hay ventas: */
            if(ventasMes.isEmpty())
             JOptionPane.showMessageDialog(this, "No hay ventas en el mes pasado.");
            
             /*Agrupacion por id */

             Map<Integer, Integer> CantVendidas = new HashMap<>();
             Map<Integer, Double> GananciasProducto = new HashMap<>();
             Map<Integer, Producto> ProductosPorID = new HashMap<>();

            for(Venta v : ventasMes){
                int id = v.getProducto().getId();
                ProductosPorID.put(id, v.getProducto());

                /*Cantidad vendida */
                CantVendidas.put(id, CantVendidas.getOrDefault(id, 0) + v.getCantidad());

                /*Calcular ganancia */
                double Ganancia = v.getCantidad() * v.getPrecioUnitario();
                GananciasProducto.put(id, GananciasProducto.getOrDefault(id, 0.0) + Ganancia);
            }

            /*Creacion de listas */

            List<Integer> MasVendidos = CantVendidas.keySet().stream()
                    .sorted((id1, id2) -> Integer.compare(CantVendidas.get(id2), CantVendidas.get(id1)))
                    .limit(5)
                    .toList();

            List<Integer> MenosVendidos =  CantVendidas.keySet().stream()
                    .sorted(Comparator.comparingInt(CantVendidas::get))
                    .limit(5)
                    .toList();

            List<Integer> MayorGanancia = GananciasProducto.keySet().stream()
                    .sorted((id1, id2) -> Double.compare(GananciasProducto.get(id2), GananciasProducto.get(id1)))
                    .limit(5)
                    .toList();
            
            /* Llenar tabla de más vendidos*/
            for (int i = 0; i < MasVendidos.size(); i++) {
                int id = MasVendidos.get(i);
                Producto p = ProductosPorID.get(id);
                tablaMasVendidos.setValueAt(p.getNombre(), i, 0);
                tablaMasVendidos.setValueAt(CantVendidas.get(id), i, 1);
            }

            /*Llenar tablas de menos vendidos */
            for (int i = 0; i < MenosVendidos.size(); i++) {
                int id = MenosVendidos.get(i);
                Producto p = ProductosPorID.get(id);
                tablaMenosVendidos.setValueAt(p.getNombre(), i, 0);
                tablaMenosVendidos.setValueAt(CantVendidas.get(id), i, 1);
            }

            /*Llenar tabla con mas ganancia */    
            for (int i = 0; i < MayorGanancia.size(); i++) {    
                int id = MayorGanancia.get(i);
                Producto p = ProductosPorID.get(id);
                tablaMayorGanancia.setValueAt(p.getNombre(), i, 0);
                tablaMayorGanancia.setValueAt(GananciasProducto.get(id), i, 1);
            }

            

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos en tablas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Fin del panel tablas

        pestanas.addTab("Tablas", PanelTablas);

        add(pestanas);
        setVisible(true);

=======
        add(pestanas);
>>>>>>> 9596c3dff79a57d762061ad282a51c7a71227398
    }
}