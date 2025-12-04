package app.vista;

import app.modelo.Deudores;
import app.modelo.DeudoresDAO;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class DeudorTableModel extends AbstractTableModel {

    private final String[] columnas = {
        "ID", "Nombre", "Producto", "Marca", "Categoría", "Cantidad", "Precio", "Costo", "Pagado"
    };

    private List<Deudores> lista;

    public DeudorTableModel(List<Deudores> lista) {
        this.lista = lista;
    }

    public DeudorTableModel() {
        this.lista = List.of();
    }

    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Deudores d = lista.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                d.getId();
            case 1 ->
                d.getNombre();
            case 2 ->
                d.getProducto();
            case 3 ->
                d.getMarca();
            case 4 ->
                d.getCategoria();
            case 5 ->
                d.getCantidad();
            case 6 ->
                d.getPrecio();
            case 7 ->
                d.getCosto();
            case 8 -> 
                d.getPagadoAsString();
            default ->
                null;
        };
    }

    // Para que el checkbox sea editable
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 8; // Editable solo Pagado
    }

    // Para guardar el cambio en el objeto Deudor
  @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == 8) {
            Deudores d = lista.get(rowIndex);
            
            // 🔥 CAMBIAR: Convertir de String ("Si"/"No") a boolean
            if (value instanceof String) {
                String textoPagado = (String) value;
                boolean nuevoEstado = Deudores.parsePagado(textoPagado);
                d.setPagado(nuevoEstado);
            } else if (value instanceof Boolean) {
                // Por si acaso todavía llega como Boolean
                d.setPagado((boolean) value);
            }

            try {
                new DeudoresDAO().actualizarDeudor(d);
            } catch (Exception e) {
                e.printStackTrace();
            }

            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }

    // Necesario para el Renderer
    public Deudores getDeudorAt(int row) {
        return lista.get(row);
    }

  @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Integer.class;    // ID
            case 5 -> Integer.class;    // Cantidad
            case 6 -> Double.class;     // Precio
            case 7 -> Double.class;     // Costo
            case 8 -> String.class;     // pagado
            default -> Object.class;
        };
    }

    @Override
    public String getColumnName(int col) {
        return columnas[col];
    }
}