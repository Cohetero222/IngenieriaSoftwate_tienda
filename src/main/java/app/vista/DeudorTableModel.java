package app.vista;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import app.modelo.Deudores;

public class DeudorTableModel extends AbstractTableModel {

    private final String[] columnas = {
            "ID", "Nombre", "Producto", "Marca", "Categoría", "Cantidad", "Precio", "Costo"
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
            case 0 -> d.getId();
            case 1 -> d.getNombre();
            case 2 -> d.getProducto();
            case 3 -> d.getMarca();
            case 4 -> d.getCategoria();
            case 5 -> d.getCantidad();
            case 6 -> d.getPrecio();
            case 7 -> d.getCosto();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int col) {
        return columnas[col];
    }
}