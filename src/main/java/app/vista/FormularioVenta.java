/*
<<<<<<< HEAD
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.vista;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

=======
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package app.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import app.modelo.DetalleVenta;
>>>>>>> HU-15
import app.modelo.Producto;
import app.modelo.ProductoDAO;
import app.modelo.Venta;
import app.modelo.VentaDAO;
<<<<<<< HEAD
=======

>>>>>>> HU-15
/**
 *
 * @author omarf
 */
public class FormularioVenta extends JDialog {
<<<<<<< HEAD
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
=======
    private ArrayList<Producto> productos;
    private ArrayList<JTextField> camposCantidades;
    private ArrayList<JTextField> camposTotales;
    private JTextField txtTotalGeneral;
    private JButton btnRegistrar, btnCancelar;

    public FormularioVenta(JFrame parent, ArrayList<Integer> idsProductos) {
        super(parent, "Registrar Venta", true);
        setPreferredSize(new Dimension(450, 380));
        setLocationRelativeTo(parent);

        productos = new ArrayList<>();
        camposCantidades = new ArrayList<>();
        camposTotales = new ArrayList<>();

        setLayout(new BorderLayout());
        JPanel panelCentral = new JPanel(new GridLayout(idsProductos.size() + 2, 3, 10, 10));

        panelCentral.add(new Label("Producto"));
        panelCentral.add(new Label("Cantidad"));
        panelCentral.add(new Label("Total"));

        try {
            ProductoDAO productoDAO = new ProductoDAO();

            for (Integer id : idsProductos) {
                Producto producto = productoDAO.buscarPorId(id);
                productos.add(producto);

                panelCentral.add(new JLabel(producto.getNombre()));

                JTextField txtCantidad = new JTextField();
                txtCantidad.setColumns(1);
                camposCantidades.add(txtCantidad);
                panelCentral.add(txtCantidad);

                JTextField txtTotal = new JTextField();
                txtTotal.setEditable(false);
                txtTotal.setColumns(2);
                camposTotales.add(txtTotal);
                panelCentral.add(txtTotal);

                /* Recualculamos el total */
                txtCantidad.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent evnt) {
                        String t = txtCantidad.getText().trim();

                        // Si está vacío total = 0
                        if (t.isEmpty()) {
                            txtTotal.setText("0.00");
                            txtTotal.setForeground(Color.BLACK);
                            actualizarTotalGeneral();
                            return;
                        }

                        try {
                            int cantidad = Integer.parseInt(t);

                            if (cantidad < 0) {
                                txtTotal.setText("ERROR");
                                txtTotal.setForeground(Color.RED);
                                actualizarTotalGeneral();
                                return;
                            }

                            double total = cantidad * producto.getPrecio();
                            txtTotal.setForeground(Color.BLACK);
                            txtTotal.setText(String.format("%.2f", total));

                        } catch (NumberFormatException e) {
                            txtTotal.setText("ERROR");
                            txtTotal.setForeground(Color.RED);
                        }
                        actualizarTotalGeneral();
                    }
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener productos: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
>>>>>>> HU-15
            dispose();
            return;
        }

<<<<<<< HEAD
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
        btnRegistrar = new JButton("✅ Registrar");
        btnCancelar = new JButton("❌ Cancelar");

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
=======
        panelCentral.add(new JLabel("Total General: "));
        panelCentral.add(new JLabel(""));
        txtTotalGeneral = new JTextField();
        txtTotalGeneral.setEditable(false);
        panelCentral.add(txtTotalGeneral);

        JScrollPane scroll = new JScrollPane(panelCentral);
        add(scroll, BorderLayout.CENTER);
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegistrar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        // Acción de los botones
        btnRegistrar.addActionListener(e -> registrarVenta());
        btnCancelar.addActionListener(e -> dispose());
        pack();
    }

    private double actualizarTotalGeneral() {
        double totalGeneral = 0.0;

        for (JTextField txtTotal : camposTotales) {
            String texto = txtTotal.getText().trim();
            if (!texto.isEmpty()) {
                texto = texto.replace(",", ".");
                try {
                    double Valor = Double.parseDouble(texto);
                    totalGeneral += Valor;
                } catch (NumberFormatException e) {
                    System.out.println("Valor invalido en un total: " + texto);
                }
            }
        }
        txtTotalGeneral.setText(String.format("%.2f", totalGeneral));
        return totalGeneral;
    }

    private void registrarVenta() {
        for (JTextField txt : camposTotales) {
            if (txt.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se puede registrar la venta por cantidades inválidas.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        int i, Cant;
        /* Verificamos que el total sea correcto */
        if (txtTotalGeneral.getText().equals("ERROR")) {
            JOptionPane.showMessageDialog(this, "No se puede registrar la venta por cantidades invalidas.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Venta venta = new Venta();
            for (i = 0; i < productos.size(); i++) {
                Producto producto = productos.get(i);
                String txt = camposCantidades.get(i).getText().trim();
                if (txt.isEmpty())
                    continue; // Cuando no hay cantidad
                Cant = Integer.parseInt(txt);
                if (Cant <= 0) {
                    continue; // Saltamos cantidades no validas
                }
                venta.AgregarDetalle(new DetalleVenta(producto, Cant, producto.getPrecio()));
            }
            VentaDAO ventaDAO = new VentaDAO();
            ventaDAO.guardarVenta(venta);
            JOptionPane.showMessageDialog(this, "Venta registrada exitosamente.");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al registrar la venta: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
>>>>>>> HU-15
        }
    }
}
