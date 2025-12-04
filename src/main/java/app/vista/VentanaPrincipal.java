package app.vista;

<<<<<<< HEAD
import javax.swing.*;

import app.modelo.Deudores;
import app.modelo.DeudoresDAO;
import app.modelo.Producto;
import app.modelo.ProductoDAO;
import java.awt.*;
=======
import java.awt.BorderLayout;
import java.awt.FlowLayout;
>>>>>>> 2fa701c4594d6045f48b7d94be4c00490f34b4e0
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

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

        // Listener de búsqueda
        txtBuscador.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                buscarProducto();
                buscarDeudor();
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
        btnRefresh.addActionListener(e -> reorganizarIDs());

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

        //Método para reorganizar los ids
private void reorganizarIDs() {
        try {
            // 1. Reorganizar IDs de productos
            ConexionSQLite.reorganizarIDsProductos();
            
            // 2. Reorganizar IDs de deudores
            ConexionSQLiteDevolver.reorganizarIDsDeudores();
            
            // 3. Mostrar confirmación
            JOptionPane.showMessageDialog(this, 
                "IDs reorganizados exitosamente\n" +
                "Productos: IDs secuenciales\n" +
                "Deudores: IDs secuenciales", 
                "Reorganización Completada", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // 4. Actualizar las tablas para mostrar los nuevos IDs
            actualizarTablaSinReorganizar();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al reorganizar IDs: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Método actualizado para actualizar sin reorganizar 
    private void actualizarTablaSinReorganizar() {
        try {
            // Actualizar ambas tablas SIN reorganizar IDs
            buscarProducto();
            List<Producto> productos = new ProductoDAO().listarTodos();
            tablaProductos.setModel(new ProductoTableModel(productos));
            tablaProductos.setDefaultRenderer(Object.class, new ProductoCellRenderer());
            
            // Actualizar tabla de deudores también
            cargarTablaDeudores();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar datos: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
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
             //FORZAR LA CONFIGURACIÓN DEL RENDERER
        tablaDeudores.setDefaultRenderer(Object.class, new DeudorCellRenderer());
        
        //  OPCIONAL: Aplicar también a tipos específicos
        tablaDeudores.setDefaultRenderer(String.class, new DeudorCellRenderer());
        tablaDeudores.setDefaultRenderer(Integer.class, new DeudorCellRenderer());
        tablaDeudores.setDefaultRenderer(Double.class, new DeudorCellRenderer());
        tablaDeudores.setDefaultRenderer(Boolean.class, new DeudorCellRenderer());
        
       
        
        //  FORZAR REPINTADO
        tablaDeudores.repaint();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error al cargar deudores: " + ex.getMessage());
    }
    }

    // ============================================================
    // PRODUCTOS
    // ============================================================

    private void registrarVenta() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            int idProducto = (int) tablaProductos.getValueAt(fila, 0);
            new FormularioVenta(this, idProducto).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para registrar la venta");
        }
    }

    private void registrarDevolucion() {
        // Abrir formulario de devolución (permite escribir nombre o seleccionar si hay
        // duplicados)
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
       //Método actualizar tabla original.
    public void actualizarTabl1() {
        actualizarTablaSinReorganizar();
    }

    //Método de actualizar tabla 2.
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

    private void buscarDeudor() {
        String texto = txtBuscador.getText().trim();

        try {
            DeudoresDAO dao = new DeudoresDAO();
            List<Deudores> deudores = texto.isEmpty() ? dao.listarTodos() : dao.buscarPorNombre(texto);

            tablaDeudores.setModel(new DeudorTableModel(deudores));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de búsqueda: " + ex.getMessage());
        }
    }
}