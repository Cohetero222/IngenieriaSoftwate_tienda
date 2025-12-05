/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.vista;

import app.modelo.Producto;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author omarf
 */
public class ProductoTableModel extends AbstractTableModel {
    private final String[] columnas = { "ID", "Nombre", "Marca", "Categoría", "Cantidad", "Precio", "Costo", "Estado",
            "Ventas",
            "Caducidad" };
    private List<Producto> productos;

    public ProductoTableModel() {
        this.productos = List.of();
    }

    public ProductoTableModel(List<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public int getRowCount() {
        return productos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Producto p = productos.get(row);
        return switch (column) {
            case 0 -> p.getId();
            case 1 -> p.getNombre();
            case 2 -> p.getMarca();
            case 3 -> p.getCategoria();
            case 4 -> p.getCantidad();
            case 5 -> String.format("$%.2f", p.getPrecio());
            case 6 -> String.format("$%.2f", p.getCosto());
            case 7 -> p.getEstado();
            case 8 -> p.getVentas();
            case 9 ->
                p.getFechaCaducidad() != null ? p.getFechaCaducidad().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "N/A";
            default -> null;
        };
    }

    public Producto getProductoEn(int fila) {
        if(fila >=0 && fila < productos.size()){
            return productos.get(fila);
        }
        return null;
    }
}
