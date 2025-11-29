package vista;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import modelo.Deudores;

public class DeudorCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        //  Obtener el Deudor directamente del TableModel**
        DeudorTableModel model = (DeudorTableModel) table.getModel();
        Deudores deudor = model.getDeudorAt(row);
        boolean pagado = deudor.isPagado(); 

        // Aplicar colores solo si NO está seleccionado
        if (!isSelected) {
            if (pagado) {
                c.setBackground(new Color(144, 238, 144)); // VERDE CLARO
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(new Color(255, 182, 193)); // ROJO CLARO
                c.setForeground(Color.BLACK);
            }
        } else {
            // Si está seleccionado, usar colores por defecto de selección
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        }

        return c;
    }
}