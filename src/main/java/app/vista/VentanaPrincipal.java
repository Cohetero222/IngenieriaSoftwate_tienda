package app.vista;

import app.modelo.Producto;
import app.modelo.ProductoDAO;
import app.modelo.ConexionSQLiteNotificaciones;
import app.modelo.Deudores;
import app.modelo.DeudoresDAO;
import app.modelo.Notificaciones;
import app.modelo.NotificacionesDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import app.modelo.ConexionSQLite;
import app.modelo.ConexionSQLiteDevolver;
import app.modelo.Deudores;
import app.modelo.DeudoresDAO;
import app.modelo.Producto;
import app.modelo.ProductoDAO;

public class VentanaPrincipal extends JFrame {

    private JTable tablaProductos;
    private JTable tablaDeudores;
    private JTable tablaNotificaciones;

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

        panelBuscador.add(new JLabel(""));
        panelBuscador.add(txtBuscador);
        add(panelBuscador, BorderLayout.NORTH);

        txtBuscador.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                buscarProducto();
                buscarDeudor();
            }
        });

        // =============================
        // 📌 TABS PRODUCTOS / DEUDORES / NOTIFICACIONES
        // =============================
        JTabbedPane tabs = new JTabbedPane();

        // TABLA PRODUCTOS
        tablaProductos = new JTable(new ProductoTableModel());
        tabs.addTab("Productos", new JScrollPane(tablaProductos));

        // TABLA DEUDORES
        tablaDeudores = new JTable(new DeudorTableModel());
        tabs.addTab("Deudores", new JScrollPane(tablaDeudores));

        // TABLA NOTIFICACIONES
        tablaNotificaciones = new JTable();
        tabs.addTab("Notificaciones", new JScrollPane(tablaNotificaciones));

        add(tabs, BorderLayout.CENTER);

        // =============================
        // BOTONES
        // =============================
        JPanel panelBotones = new JPanel();

        btnAgregar = new JButton(" Agregar Producto");
        btnEditar = new JButton(" Editar");
        btnEliminar = new JButton(" Eliminar");
        btnReportes = new JButton(" Reportes");
        btnRegistrarVenta = new JButton(" Registrar Venta");
        btnDeudores = new JButton(" Registrar/Editar Deudor");
        btnDevolucion = new JButton(" Devolución");
        // Boton nuevo para refrescar la base de datos.
        btnRefresh = new JButton(" Refresh");

        btnAgregar.addActionListener(e -> abrirFormularioProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnReportes.addActionListener(e -> abrirReportes());
        btnRegistrarVenta.addActionListener(e -> registrarVenta());
        btnDevolucion.addActionListener(e -> registrarDevolucion());
        // Parte para refrescar.
        btnRefresh.addActionListener(e -> actualizarTabla());

        btnEliminar.addActionListener(e -> {
            int tab = tabs.getSelectedIndex();
            if (tab == 0) eliminarProducto();
            else eliminarDeudor();
        });

        btnReportes.addActionListener(e -> abrirReportes());
        btnRegistrarVenta.addActionListener(e -> registrarVenta());
        btnDevolucion.addActionListener(e -> registrarDevolucion());
        btnDeudores.addActionListener(e -> registrarDeudor());

        // Refresh reorganiza IDs
        btnRefresh.addActionListener(e -> reorganizarIDs());

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
        generarNotificacionesAutomaticas();
        cargarNotificaciones();
    }

    // =============================
    // REORGANIZAR IDs
    // =============================
    private void reorganizarIDs() {
        try {
            ConexionSQLite.reorganizarIDsProductos();
            ConexionSQLiteDevolver.reorganizarIDsDeudores();

            JOptionPane.showMessageDialog(this,
                    "IDs reorganizados exitosamente\n" +
                    "Productos y Deudores ahora tienen IDs secuenciales.",
                    "Reorganización Completa",
                    JOptionPane.INFORMATION_MESSAGE);

            actualizarTablaSinReorganizar();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al reorganizar IDs: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTablaSinReorganizar() {
        try {
            buscarProducto();
            List<Producto> productos = new ProductoDAO().listarTodos();
            tablaProductos.setModel(new ProductoTableModel(productos));
            tablaProductos.setDefaultRenderer(Object.class, new ProductoCellRenderer());

            cargarTablaDeudores();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =============================
    // 📌 MÉTODOS DE DEUDORES
    // =============================
    private void registrarDeudor() {
        int fila = tablaDeudores.getSelectedRow();

        if (fila >= 0) {
            int idDeudor = (int) tablaDeudores.getValueAt(fila, 0);

            try {
                Deudores d = new DeudoresDAO().buscarPorId(idDeudor);
                new FormularioDeudor(this, d).setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar deudor: " + e.getMessage());
            }

        } else {
            new FormularioDeudor(this).setVisible(true);
        }

        cargarTablaDeudores();
    }

    private void cargarTablaDeudores() {
        try {
            List<Deudores> lista = new DeudoresDAO().listarTodos();
            tablaDeudores.setModel(new DeudorTableModel(lista));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar deudores: " + ex.getMessage());
        }
    }

    private void eliminarDeudor() {
        int fila = tablaDeudores.getSelectedRow();
        if (fila >= 0) {
            int id = (int) tablaDeudores.getValueAt(fila, 0);

            try {
                new DeudoresDAO().eliminar(id);
                cargarTablaDeudores();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void buscarDeudor() {
        String texto = txtBuscador.getText().trim();

        try {
            DeudoresDAO dao = new DeudoresDAO();
            List<Deudores> deudores = texto.isEmpty()
                    ? dao.listarTodos()
                    : dao.buscarPorNombre(texto);

            tablaDeudores.setModel(new DeudorTableModel(deudores));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de búsqueda: " + ex.getMessage());
        }
    }

    // =============================
    // PRODUCTOS
    // =============================
    private void registrarVenta() {
        int[] filas = tablaProductos.getSelectedRows();
        if (filas.length > 0) {
            ArrayList<Integer> ids = new ArrayList<>();
            for (int f : filas) ids.add((int) tablaProductos.getValueAt(f, 0));

            new FormularioVenta(this, ids).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione uno o más productos.");
        }

        generarNotificacionesAutomaticas();
        cargarNotificaciones();
    }

    private void registrarDevolucion() {
        new FormularioDevolucion(this).setVisible(true);
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
            JOptionPane.showMessageDialog(this, "Seleccione un producto.");
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

    public void actualizarTabl1() {
        actualizarTablaSinReorganizar();
    }

    public void actualizarTabl2() {
        actualizarTablaSinReorganizar();
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

            generarNotificacionesAutomaticas();
            cargarNotificaciones();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage());
        }
    }

    private void buscarProducto() {
        String texto = txtBuscador.getText().trim();

        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = texto.isEmpty()
                    ? dao.listarTodos()
                    : dao.buscarPorNombre(texto);

            tablaProductos.setModel(new ProductoTableModel(productos));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de búsqueda: " + ex.getMessage());
        }
    }

    // ============================================================
    // NOTIFICACIONES
    // ============================================================    

    private void generarNotificacionesAutomaticas() {
        try {
            new NotificacionesDAO().VerificarNotificacionesAutomaticas();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar notificaciones automáticas: " + ex.getMessage());
        }
    }

    private void cargarNotificaciones() {
        try {
            List<Notificaciones> lista = new NotificacionesDAO().listarTodas();
            NotificacionesTableModel model = new NotificacionesTableModel(lista);
            tablaNotificaciones.setModel(model);

            // Pintar filas en color Rojo

            tablaNotificaciones.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                boolean isSelected, boolean hasFocus, int row, int column){
                                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                                    boolean revisada = (boolean) table.getValueAt(row, 4);

                                    if (!revisada) {
                                        c.setBackground(new Color(255, 0, 0));
                                        c.setForeground(Color.white);
                                    } else {
                                        c.setBackground(Color.WHITE);
                                    }

                                    if (isSelected) {
                                        c.setBackground(new Color(180, 0, 0));
                                        c.setForeground(Color.WHITE);
                                    }

                                    return c;
                                }
            });

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay productos con cantidad menor a 5",
                                                "Notificaciones", JOptionPane.INFORMATION_MESSAGE);
            }

            tablaNotificaciones.getModel().addTableModelListener(e -> {
                int fila = e.getFirstRow();
                int columna = e.getColumn();

                if (fila < 0) return;

                if (columna == 4) {
                    Notificaciones n = model.getNotificacion(fila);

                    if (n.isRevisada()) {
                        try {
                            new NotificacionesDAO().eliminarNotificacion(n.getId());
                            model.removeRow(fila);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Error al marcar revisada: " + ex.getMessage());
                        }
                    }
                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar notificaciones: " + ex.getMessage());
        }
    } 
}
