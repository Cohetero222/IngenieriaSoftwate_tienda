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

    public VentanaPrincipal() {
        setTitle("Gestión de Tienda");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
            JOptionPane.showMessageDialog(this, "Seleccione un producto para registrar la venta", "Error", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirReportes() {
        new DialogoReportes(this).setVisible(true);
    }

    public void actualizarTabla() {
        try {
            List<Producto> productos = new ProductoDAO().listarTodos();
            tablaProductos.setModel(new ProductoTableModel(productos));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
