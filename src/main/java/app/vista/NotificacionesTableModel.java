package app.vista;

import app.modelo.Notificaciones;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class NotificacionesTableModel extends AbstractTableModel {

    private final String[] columnas = {
            "ID", "Producto", "Cantidad actual", "Cantidad mínima", "Revisado"
    };

    private List<Notificaciones> lista;

    public NotificacionesTableModel(List<Notificaciones> lista) {
        this.lista = lista;
    }

    public void removeRow(int row) {
        if (row < 0 || row >= lista.size()) return;
        lista.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public int getRowCount() { return lista.size(); }

    @Override
    public int getColumnCount() { return columnas.length; }

    @Override
    public String getColumnName(int col) { return columnas[col]; }

    @Override
    public Class<?> getColumnClass(int col) {
        return switch (col) {
            case 4 -> Boolean.class;
            default -> Object.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Notificaciones n = lista.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> n.getId();
            case 1 -> n.getProductoNombre();
            case 2 -> n.getCantidadActual();
            case 3 -> n.getCantidadMinima();
            case 4 -> n.isRevisada();
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 4; // checkbox editable
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 4) {
            lista.get(row).setRevisada((Boolean) value);
            fireTableCellUpdated(row, col);
        }
    }

    public Notificaciones getNotificacion(int row) {
        return lista.get(row);
    }
}
