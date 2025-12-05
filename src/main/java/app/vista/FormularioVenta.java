/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import app.modelo.Producto;
import app.modelo.ProductoDAO;
import app.modelo.DetalleVenta;
import app.modelo.Venta;
import app.modelo.VentaDAO;

/**
 *
 * @author omarf
 */
public class FormularioVenta extends JDialog {

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

        panelCentral.add(new JLabel("Producto"));
        panelCentral.add(new JLabel("Cantidad"));
        panelCentral.add(new JLabel("Total"));

        try {
            ProductoDAO productoDAO = new ProductoDAO();

            for (Integer id : idsProductos) {
                Producto producto = productoDAO.buscarPorId(id);
                productos.add(producto);

                panelCentral.add(new JLabel(producto.getNombre()));

                JTextField txtCantidad = new JTextField();
                camposCantidades.add(txtCantidad);
                panelCentral.add(txtCantidad);

                JTextField txtTotal = new JTextField();
                txtTotal.setEditable(false);
                camposTotales.add(txtTotal);
                panelCentral.add(txtTotal);

                // Recalcular total
                txtCantidad.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        String value = txtCantidad.getText().trim();

                        if (value.isEmpty()) {
                            txtTotal.setText("0.00");
                            txtTotal.setForeground(Color.BLACK);
                            actualizarTotalGeneral();
                            return;
                        }

                        try {
                            int cantidad = Integer.parseInt(value);

                            if (cantidad < 0) {
                                txtTotal.setText("ERROR");
                                txtTotal.setForeground(Color.RED);
                                actualizarTotalGeneral();
                                return;
                            }

                            double total = cantidad * producto.getPrecio();
                            txtTotal.setForeground(Color.BLACK);
                            txtTotal.setText(String.format("%.2f", total));

                        } catch (NumberFormatException ex) {
                            txtTotal.setText("ERROR");
                            txtTotal.setForeground(Color.RED);
                        }

                        actualizarTotalGeneral();
                    }
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

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

        btnRegistrar.addActionListener(e -> registrarVenta());
        btnCancelar.addActionListener(e -> dispose());

        pack();
    }

    private double actualizarTotalGeneral() {
        double total = 0.0;

        for (JTextField txtTotal : camposTotales) {
            String text = txtTotal.getText().trim();
            if (!text.isEmpty() && !text.equals("ERROR")) {
                try {
                    total += Double.parseDouble(text.replace(",", "."));
                } catch (NumberFormatException ignored) {}
            }
        }

        txtTotalGeneral.setText(String.format("%.2f", total));
        return total;
    }

    private void registrarVenta() {

        // Evitar registrar si hay errores
        for (JTextField t : camposTotales) {
            if (t.getText().equals("ERROR")) {
                JOptionPane.showMessageDialog(this,
                        "No se puede registrar la venta: hay cantidades inválidas.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            Venta venta = new Venta();

            for (int i = 0; i < productos.size(); i++) {
                Producto producto = productos.get(i);

                String tx = camposCantidades.get(i).getText().trim();
                if (tx.isEmpty()) continue;

                int cantidad = Integer.parseInt(tx);
                if (cantidad <= 0) continue;

                venta.AgregarDetalle(new DetalleVenta(
                        producto,
                        cantidad,
                        producto.getPrecio()
                ));
            }

            new VentaDAO().guardarVenta(venta);

            JOptionPane.showMessageDialog(this,
                    "Venta registrada exitosamente.");

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar la venta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
