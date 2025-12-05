/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import app.modelo.Producto;
import app.modelo.ProductoDAO;
import app.modelo.Venta;
import app.modelo.VentaDAO;
/**
 *
 * @author omarf
 */
public class FormularioVenta extends JDialog {
    private JTextField txtNombreProducto, txtPrecioUnitario, txtCantidad, txtTotal;
    private JButton btnRegistrar, btnCancelar;
    private Producto producto;

    public FormularioVenta(JFrame parent, int idProducto) {
        super(parent, "Registrar Venta", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2, 10, 10));

        try {
            this.producto = new ProductoDAO().buscarPorId(idProducto); // Asumes que tienes este método
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Campos
        add(new JLabel("Producto:"));
        txtNombreProducto = new JTextField(producto.getNombre());
        txtNombreProducto.setEditable(false);
        add(txtNombreProducto);

        add(new JLabel("Precio Unitario:"));
        txtPrecioUnitario = new JTextField(String.valueOf(producto.getPrecio()));
        txtPrecioUnitario.setEditable(false);
        add(txtPrecioUnitario);

        add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField();
        add(txtCantidad);

        add(new JLabel("Total:"));
        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        add(txtTotal);

        // Botones
        btnRegistrar = new JButton(" Registrar");
        btnCancelar = new JButton(" Cancelar");

        add(btnRegistrar);
        add(btnCancelar);

        // Eventos
        txtCantidad.addActionListener(e -> calcularTotal());
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calcularTotal();
            }
        });

        btnRegistrar.addActionListener(e -> registrarVenta());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void calcularTotal() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText());
            double total = cantidad * producto.getPrecio();
            txtTotal.setText(String.format("%.2f", total));
        } catch (NumberFormatException e) {
            txtTotal.setText("");
        }
    }

    private void registrarVenta() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText());
            if (cantidad <= 0) throw new NumberFormatException();

            Venta venta = new Venta(producto, cantidad);
            new VentaDAO().guardarVenta(venta); // Necesitas implementar esto

            JOptionPane.showMessageDialog(this, "Venta registrada:\n" + venta);
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la venta: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
