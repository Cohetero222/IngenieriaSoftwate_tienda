package app.vista;


import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import app.modelo.Producto;

public class ProductoCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (table.getModel() instanceof ProductoTableModel model) {
            Producto producto = model.getProductoEn(row);

            if (producto != null && producto.getFechaCaducidad() != null) {
                LocalDate hoy = LocalDate.now();
                long diasRestantes = ChronoUnit.DAYS.between(hoy, producto.getFechaCaducidad());

                if (diasRestantes < 0) {
                    // Se pinta en Rojo la fila
                    c.setBackground(Color.RED);
                    c.setForeground(Color.WHITE);
                } else if (diasRestantes <= 7) {
                    // Se pinta de Naranja la fila
                    c.setBackground(Color.ORANGE);
                    c.setForeground(Color.BLACK);
                } else {
                    // Se queda en Blanco
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            }
        }

        // Mantener colores de seleccion
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        }

        return c;

    }

}
