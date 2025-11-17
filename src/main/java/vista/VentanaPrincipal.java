/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import modelo.Producto;
import modelo.ProductoDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import modelo.Venta;
import modelo.VentaDAO;
import vista.FormularioVenta;

/**
 *
 * @author omarf
 */
public class VentanaPrincipal extends JFrame {
    private JTable tablaProductos;
    private JButton btnAgregar, btnEditar, btnEliminar, btnReportes, btnRegistrarVenta;
    private JTextField txtBuscador;

    public VentanaPrincipal() {
        setTitle("Gestión de Tienda");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // =======================================================
        // Panel del Buscador (Sección NORTH)
        // =======================================================
        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.CENTER));
        txtBuscador = new JTextField(25);

        panelBuscador.add(new JLabel("🔎"));
        panelBuscador.add(txtBuscador);
        add(panelBuscador, BorderLayout.NORTH);

        // Agregar el listener al buscador
        txtBuscador.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                buscarProducto();
            }
        });

        // Tabla de productos
        tablaProductos = new JTable(new ProductoTableModel());
        add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        btnAgregar = new JButton("➕ Agregar");
        btnEditar = new JButton("✏️ Editar");
        btnEliminar = new JButton("❌ Eliminar");
        btnReportes = new JButton("📊 Reportes");
        btnRegistrarVenta = new JButton("💵 Registrar Venta");

        btnAgregar.addActionListener(e -> abrirFormularioProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnReportes.addActionListener(e -> abrirReportes());
        btnRegistrarVenta.addActionListener(e -> registrarVenta());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReportes);
        panelBotones.add(btnRegistrarVenta);
        add(panelBotones, BorderLayout.SOUTH);

        // Cargar datos
        actualizarTabla();
    }

    private void registrarVenta() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            int idProducto = (int) tablaProductos.getValueAt(fila, 0);
            new FormularioVenta(this, idProducto).setVisible(true); // Debes implementar esta clase
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para registrar la venta", "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
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
            JOptionPane.showMessageDialog(this, "Seleccione un producto", "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
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
            //Reaplicar renderer y repintar cada vez que cambie un modelo
            tablaProductos.setDefaultRenderer(Object.class, new ProductoCellRenderer());
            tablaProductos.revalidate();
            tablaProductos.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Buscador
    private void buscarProducto() {
        String textoBusqueda = txtBuscador.getText().trim();

        try {
            List<Producto> productos;
            ProductoDAO dao = new ProductoDAO();

            if (textoBusqueda.isEmpty()) {
                // Si el campo está vacío, listar todos (como actualización normal)
                productos = dao.listarTodos();
            } else {
                // Si hay texto, buscar usando el nuevo método DAO
                productos = dao.buscarPorNombre(textoBusqueda);
            }

            // Actualizar la tabla con los resultados (o la lista completa)
            tablaProductos.setModel(new ProductoTableModel(productos));

        } catch (SQLException ex) {     
            JOptionPane.showMessageDialog(this, "Error de búsqueda en la BD: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
