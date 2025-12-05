package app.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import app.modelo.Deudores;
import app.modelo.DeudoresDAO;

public class FormularioDeudor extends JDialog {

    private JTextField txtNombre, txtProducto, txtMarca, txtCategoria,
            txtCantidad, txtPrecio, txtCosto;
     
    private JCheckBox chkPagado; //CheckBox para Pagado
    private JButton btnGuardar, btnCancelar;

    private Deudores deudor;

    // ?? CONSTRUCTOR PRINCIPAL (editar o nuevo)
    public FormularioDeudor(JFrame parent, Deudores d) {
        super(parent, "Registrar Deudor", true);

        this.deudor = (d != null) ? d : new Deudores();

        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(10, 2, 10, 10));

         // Panel principal con los campos en GridLayout
        JPanel panelCampos = new JPanel(new GridLayout(9, 2, 10, 10));

        add(new JLabel("Nombre del deudor:"));
        txtNombre = new JTextField(deudor.getNombre());
        add(txtNombre);

        add(new JLabel("Producto:"));
        txtProducto = new JTextField(deudor.getProducto());
        add(txtProducto);

        add(new JLabel("Marca:"));
        txtMarca = new JTextField(deudor.getMarca());
        add(txtMarca);

        add(new JLabel("Categoría:"));
        txtCategoria = new JTextField(deudor.getCategoria());
        add(txtCategoria);

        add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField(String.valueOf(deudor.getCantidad()));
        add(txtCantidad);

        add(new JLabel("Precio:"));
        txtPrecio = new JTextField(String.valueOf(deudor.getPrecio()));
        add(txtPrecio);

        add(new JLabel("Costo:"));
        txtCosto = new JTextField(String.valueOf(deudor.getCosto()));
        add(txtCosto);

        // Nuevo apartado pagado 
        add(new JLabel("Pagado:"));
        chkPagado = new JCheckBox("¿Ya pagó?");
        chkPagado.setSelected(deudor.isPagado());
        add(chkPagado);

       // Panel para botones alineados a la derecha
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton(" Guardar");
        btnCancelar = new JButton(" Cancelar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        // Agregar paneles al diálogo
        add(panelCampos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
    }

    // 👉 CONSTRUCTOR PARA *NUEVO* DEUDOR
    public FormularioDeudor(JFrame parent) {
        this(parent, null);
    }

    private void guardar() {
        try {
            deudor.setNombre(txtNombre.getText());
            deudor.setProducto(txtProducto.getText());
            deudor.setMarca(txtMarca.getText());
            deudor.setCategoria(txtCategoria.getText());
            deudor.setCantidad(Integer.parseInt(txtCantidad.getText()));
            deudor.setPrecio(Double.parseDouble(txtPrecio.getText()));
            deudor.setCosto(Double.parseDouble(txtCosto.getText()));
            deudor.setPagado(chkPagado.isSelected()); 

            DeudoresDAO dao = new DeudoresDAO();

            if (deudor.getId() == 0) {
                dao.agregarDeudor(deudor);
                JOptionPane.showMessageDialog(this, "Deudor registrado correctamente.");
            } else {
                dao.actualizarDeudor(deudor);
                JOptionPane.showMessageDialog(this, "Deudor actualizado.");
            }

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar deudor: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}