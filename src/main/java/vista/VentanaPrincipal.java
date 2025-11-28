package vista;

import modelo.Producto;
import modelo.ProductoDAO;
import modelo.Deudores;
import modelo.DeudoresDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class VentanaPrincipal extends JFrame {

    private JTable tablaProductos;
    private JTable tablaDeudores;

    private JButton btnAgregar, btnEditar, btnEliminar, btnReportes, btnRegistrarVenta, btnDeudores, btnRefresh;
    private JButton btnDevolucion;
    private JTextField txtBuscador;

    public VentanaPrincipal() {
        setTitle("Gestión de Tienda");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel del Buscador
        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.CENTER));
        txtBuscador = new JTextField(25);

        panelBuscador.add(new JLabel("🔎"));
        panelBuscador.add(txtBuscador);
        add(panelBuscador, BorderLayout.NORTH);

        // Listener de búsqueda
        txtBuscador.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                buscarProducto();
            }
        });

        // =============================
        // 📌 TABS PRODUCTOS / DEUDORES
        // =============================
        JTabbedPane tabs = new JTabbedPane();

        // TABLA PRODUCTOS
        tablaProductos = new JTable(new ProductoTableModel());
        tabs.addTab("Productos", new JScrollPane(tablaProductos));

        // TABLA DEUDORES
        tablaDeudores = new JTable(new DeudorTableModel());
        tabs.addTab("Deudores", new JScrollPane(tablaDeudores));

        add(tabs, BorderLayout.CENTER);

        // =============================
        // BOTONES
        // =============================
        JPanel panelBotones = new JPanel();

        btnAgregar = new JButton("➕ Agregar Producto");
        btnEditar = new JButton("✏️ Editar");
        btnEliminar = new JButton("❌ Eliminar");
        btnReportes = new JButton("📊 Reportes");
        btnRegistrarVenta = new JButton("💵 Registrar Venta");
        btnDeudores = new JButton("💱 Registrar/Editar Deudor");
        btnDevolucion = new JButton("🔁 Devolución");
        // Boton nuevo para refrescar la base de datos.
        btnRefresh = new JButton("🔄 Refresh");

        btnAgregar.addActionListener(e -> abrirFormularioProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnReportes.addActionListener(e -> abrirReportes());
        btnRegistrarVenta.addActionListener(e -> registrarVenta());
        btnDevolucion.addActionListener(e -> registrarDevolucion());
        // Parte para refrescar.
        btnRefresh.addActionListener(e -> actualizarTabla());

        // BOTÓN PARA ABRIR FORMULARIO DE DEUDORES
        btnDeudores.addActionListener(e -> registrarDeudor());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReportes);
        panelBotones.add(btnRegistrarVenta);
        panelBotones.add(btnDevolucion);
        panelBotones.add(btnDeudores);
        // Se agrega el boton refresh al panel.
        panelBotones.add(btnRefresh);

        add(panelBotones, BorderLayout.SOUTH);

        // Cargar datos
        actualizarTabla();
        cargarTablaDeudores();
    }

    // ============================================================
    // 📌 MÉTODOS DE DEUDORES
    // ============================================================

    private void registrarDeudor() {
        int fila = tablaDeudores.getSelectedRow();

        if (fila >= 0) {
            // Editar deudor
            int idDeudor = (int) tablaDeudores.getValueAt(fila, 0);

            try {
                Deudores d = new DeudoresDAO().buscarPorId(idDeudor);
                new FormularioDeudor(this, d).setVisible(true);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar deudor: " + e.getMessage());
            }

        } else {
            // Nuevo deudor
            new FormularioDeudor(this).setVisible(true);
        }

        cargarTablaDeudores(); // refrescar
    }

    private void cargarTablaDeudores() {
        try {
            List<Deudores> lista = new DeudoresDAO().listarTodos();
            tablaDeudores.setModel(new DeudorTableModel(lista));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar deudores: " + ex.getMessage());
        }
    }

    // ============================================================
    // PRODUCTOS
    // ============================================================

    private void registrarVenta() {
        int[] filas = tablaProductos.getSelectedRows();
        if (filas.length > 0) {
            ArrayList<Integer> idsProductos = new ArrayList<>();
                for(int fila : filas){
                    int idProducto = (int) tablaProductos.getValueAt(fila, 0);
                    idsProductos.add(idProducto);
                }
                new FormularioVenta(this, idsProductos).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para registrar la venta");
        }
    }

    private void registrarDevolucion() {
        // Abrir formulario de devolución (permite escribir nombre o seleccionar si hay duplicados)
        new FormularioDevolucion(this).setVisible(true);
        // Refrescar tabla después de la posible devolución
        actualizarTabla();
    }

    private void abrirFormularioProducto() {
        new FormularioProducto(this).setVisible(true);
    }

    private void editarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            int id = (int) tablaProductos.getValueAt(fila, 0);
            new FormularioProducto(this, id).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto");
        }
    }

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            int id = (int) tablaProductos.getValueAt(fila, 0);
            try {
                new ProductoDAO().eliminarProducto(id);
                actualizarTabla();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void abrirReportes() {
        new DialogoReportes(this).setVisible(true);
    }

    public void actualizarTabla() {
        buscarProducto();
        try {
            List<Producto> productos = new ProductoDAO().listarTodos();
            tablaProductos.setModel(new ProductoTableModel(productos));
            tablaProductos.setDefaultRenderer(Object.class, new ProductoCellRenderer());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage());
        }
    }

    public void actualizarTabla2() {
        try {
            // Limpiar el buscador para mostrar todos los productos
            txtBuscador.setText("");

            // Recargar todos los productos desde la base de datos
            List<Producto> productos = new ProductoDAO().listarTodos();
            tablaProductos.setModel(new ProductoTableModel(productos));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar los datos: " + ex.getMessage(),
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarProducto() {
        String texto = txtBuscador.getText().trim();

        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = texto.isEmpty() ? dao.listarTodos() : dao.buscarPorNombre(texto);

            tablaProductos.setModel(new ProductoTableModel(productos));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de búsqueda: " + ex.getMessage());
        }
    }
}