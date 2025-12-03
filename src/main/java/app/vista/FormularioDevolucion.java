package app.vista;

import app.modelo.Producto;
import app.modelo.ProductoDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Diálogo para registrar devoluciones de productos.
 * Permite buscar por nombre, seleccionar entre coincidencias (marca - nombre)
 * y especificar la cantidad a devolver. Valida que las ventas no queden negativas.
 */
public class FormularioDevolucion extends JDialog {
    private JTextField txtNombre;
    private JButton btnBuscar;
    private JComboBox<String> comboMatches;
    private JTextField txtCantidad;
    private JButton btnDevolver, btnCancelar;

    private List<Producto> matches;
    private Producto seleccionado;

    public FormularioDevolucion(JFrame parent) {
        super(parent, "Registrar Devolución", true);
        setSize(450, 240);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; add(new JLabel("Nombre del producto:"), c);
        txtNombre = new JTextField();
        c.gridx = 1; c.gridy = 0; c.gridwidth = 2; add(txtNombre, c);

        btnBuscar = new JButton("🔎 Buscar");
        c.gridx = 3; c.gridy = 0; c.gridwidth = 1; add(btnBuscar, c);

        c.gridx = 0; c.gridy = 1; add(new JLabel("Coincidencias:"), c);
        comboMatches = new JComboBox<>();
        comboMatches.setPrototypeDisplayValue("MMMMMMMMMMMMMMMMM");
        c.gridx = 1; c.gridy = 1; c.gridwidth = 3; add(comboMatches, c);

        c.gridx = 0; c.gridy = 2; add(new JLabel("Cantidad a devolver:"), c);
        txtCantidad = new JTextField();
        c.gridx = 1; c.gridy = 2; c.gridwidth = 3; add(txtCantidad, c);

        btnDevolver = new JButton("🔁 Devolver");
        btnCancelar = new JButton("❌ Cancelar");

        c.gridx = 1; c.gridy = 3; c.gridwidth = 1; add(btnDevolver, c);
        c.gridx = 2; c.gridy = 3; add(btnCancelar, c);

        // Acciones
        btnBuscar.addActionListener(this::buscarProductos);
        comboMatches.addActionListener(e -> onSeleccion());
        btnDevolver.addActionListener(e -> procesarDevolucion(parent));
        btnCancelar.addActionListener(e -> dispose());
    }

    private void buscarProductos(ActionEvent ev) {
        String texto = txtNombre.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre (parcial) del producto a buscar.");
            return;
        }

        try {
            matches = new ProductoDAO().buscarPorNombre(texto);
            comboMatches.removeAllItems();

            if (matches.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron productos con ese nombre.");
                seleccionado = null;
                return;
            }

            for (Producto p : matches) {
                String label = (p.getMarca() != null && !p.getMarca().isBlank() ? p.getMarca() + " - " : "")
                        + p.getNombre() + " (id:" + p.getId() + ")";
                comboMatches.addItem(label);
            }

            // Seleccionar el primer elemento por defecto
            comboMatches.setSelectedIndex(0);
            seleccionado = matches.get(0);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar productos: " + ex.getMessage());
        }
    }

    private void onSeleccion() {
        int idx = comboMatches.getSelectedIndex();
        if (matches != null && idx >= 0 && idx < matches.size()) {
            seleccionado = matches.get(idx);
        }
    }

    private void procesarDevolucion(JFrame parent) {
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto primero (buscar y elegir una coincidencia).");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida (entero mayor que 0).");
            return;
        }

        try {
            boolean ok = new ProductoDAO().devolverProducto(seleccionado.getId(), cantidad);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Devolución registrada correctamente.");
                // Refrescar la tabla principal si es VentanaPrincipal
                if (parent instanceof VentanaPrincipal) {
                    ((VentanaPrincipal) parent).actualizarTabla();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar la devolución.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al procesar la devolución: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
