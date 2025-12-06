/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.vista;

import app.modelo.Producto;
import app.modelo.ProductoDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

import app.modelo.Producto;
import app.modelo.ProductoDAO;

/**
 *
 * @author omarf
 */
public class FormularioProducto extends JDialog {

    // Componentes existentes
    private JTextField txtNombre, txtMarca;
    JTextField txtPrecio;
    JTextField txtCosto;
    private JComboBox<String> cbCategoria, cbEstado;
    private JSpinner spCantidad;
    private JSpinner.DateEditor deFechaCaducidad;
    private JButton btnGuardar;

    // Nuevos componentes para ventas
    private JButton btnRegistrarVenta;
    private JSpinner spCantidadVenta;
    private Producto producto;
    private boolean esEdicion;

    public FormularioProducto(JFrame parent) {
        this(parent, -1); // Constructor para nuevo producto
    }

    public FormularioProducto(JFrame parent, int idProducto) {
        super(parent, idProducto < 0 ? "Nuevo Producto" : "Editar Producto", true);
        setSize(500, 500); 
        setLocationRelativeTo(parent);

        this.esEdicion = idProducto >= 0;

        if (esEdicion) {
            cargarProducto(idProducto);
        } else {
            this.producto = new Producto();
        }

        initComponents();
    }

    private void cargarProducto(int id) {
        try {
            this.producto = new ProductoDAO().buscarPorId(id);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar producto: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void initComponents() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel de datos del producto
        JPanel panelDatos = new JPanel(new GridLayout(9, 2, 5, 5));

        panelDatos.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(producto.getNombre());
        panelDatos.add(txtNombre);

        panelDatos.add(new JLabel("Marca:"));
        txtMarca = new JTextField(producto.getMarca());
        panelDatos.add(txtMarca);

        panelDatos.add(new JLabel("Categoría:"));
        cbCategoria = new JComboBox<>(new String[] { "Alimentos", "Bebidas", "Limpieza", "Otros" });
        cbCategoria.setSelectedItem(producto.getCategoria());
        panelDatos.add(cbCategoria);

        panelDatos.add(new JLabel("Cantidad en Stock:"));
        spCantidad = new JSpinner(new SpinnerNumberModel(producto.getCantidad(), 0, 9999, 1));
        panelDatos.add(spCantidad);

        panelDatos.add(new JLabel("Estado:"));
        cbEstado = new JComboBox<>(new String[] { "Disponible", "Agotado", "Caducado", "Descontinuado" });
        cbEstado.setSelectedItem(producto.getEstado());
        panelDatos.add(cbEstado);

        panelDatos.add(new JLabel("Precio Unitario:"));
        txtPrecio = new JTextField(producto.getPrecio() > 0 ? String.valueOf(producto.getPrecio()) : "");
        panelDatos.add(txtPrecio);

        panelDatos.add(new JLabel("Costo:"));
        txtCosto = new JTextField(producto.getCosto() > 0 ? String.valueOf(producto.getCosto()) : "");
        panelDatos.add(txtCosto);

        panelDatos.add(new JLabel("Fecha Caducidad:"));
        JSpinner spinnerFecha = new JSpinner(new SpinnerDateModel());
        deFechaCaducidad = new JSpinner.DateEditor(spinnerFecha, "dd-MM-yyyy");
        spinnerFecha.setEditor(deFechaCaducidad);
        if (producto.getFechaCaducidad() != null) {
            spinnerFecha.setValue(java.sql.Date.valueOf(producto.getFechaCaducidad()));
        }
        panelDatos.add(spinnerFecha);

        panelPrincipal.add(panelDatos, BorderLayout.CENTER);

        // Panel para registro de ventas en modo edición
        if (esEdicion) {
            JPanel panelVentas = new JPanel(new GridLayout(3, 2, 5, 5));
            panelVentas.setBorder(BorderFactory.createTitledBorder("Registrar Venta"));

            panelVentas.add(new JLabel("Cantidad a vender:"));
            spCantidadVenta = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
            panelVentas.add(spCantidadVenta);

            panelVentas.add(new JLabel("Total:"));
            JLabel lblTotal = new JLabel("$0.00");
            panelVentas.add(lblTotal);

            btnRegistrarVenta = new JButton("Registrar Venta");
            btnRegistrarVenta.addActionListener(e -> registrarVenta(lblTotal));
            panelVentas.add(btnRegistrarVenta);

            // Actualizar total
            spCantidadVenta.addChangeListener(e -> {
                double precio = producto.getPrecio();
                int cantidad = (int) spCantidadVenta.getValue();
                lblTotal.setText(String.format("$%.2f", precio * cantidad));
            });

            panelPrincipal.add(panelVentas, BorderLayout.SOUTH);
        }

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton(esEdicion ? "Actualizar" : "Guardar");
        btnGuardar.addActionListener(this::guardarProducto);
        panelBotones.add(btnGuardar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);

        panelPrincipal.add(panelBotones, BorderLayout.PAGE_END);

        add(panelPrincipal);
    }

    private void guardarProducto(ActionEvent e) {
        try {
            java.util.Date fecha = (java.util.Date) ((JSpinner) deFechaCaducidad.getSpinner()).getValue();

            if (fecha == null) {
                JOptionPane.showMessageDialog(this,
                        "El campo 'Fecha Caducidad' es obligatorio.",
                        "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            producto.setNombre(txtNombre.getText());
            producto.setMarca(txtMarca.getText());
            producto.setCategoria((String) cbCategoria.getSelectedItem());
            producto.setCantidad((int) spCantidad.getValue());
            producto.setEstado((String) cbEstado.getSelectedItem());
            producto.setPrecio(Double.parseDouble(txtPrecio.getText()));
            producto.setCosto(Double.parseDouble(txtCosto.getText()));

            producto.setFechaCaducidad(new java.sql.Date(fecha.getTime()).toLocalDate());

            ProductoDAO dao = new ProductoDAO();
            if (esEdicion) {
                dao.actualizarProducto(producto);
            } else {
                dao.agregarProducto(producto);
            }

            ((VentanaPrincipal) getParent()).actualizarTabla();
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y Costo deben ser números válidos", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registrarVenta(JLabel lblTotal) {
        int cantidad = (int) spCantidadVenta.getValue();

        try {
            if (cantidad > producto.getCantidad()) {
                JOptionPane.showMessageDialog(this,
                        "No hay suficiente stock. Disponible: " + producto.getCantidad(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ProductoDAO dao = new ProductoDAO();
            if (dao.registrarVenta(producto.getId(), cantidad)) {
                producto.setCantidad(producto.getCantidad() - cantidad);
                producto.setVentas(producto.getVentas() + cantidad);
                spCantidad.setValue(producto.getCantidad());

                JOptionPane.showMessageDialog(this,
                        "Venta registrada exitosamente\nTotal: " + lblTotal.getText(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                ((VentanaPrincipal) getParent()).actualizarTabla();
            } else {
                throw new SQLException("No se pudo registrar la venta");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar venta: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
