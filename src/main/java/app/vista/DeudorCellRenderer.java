package app.vista;

import app.modelo.Deudores;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderizador personalizado para celdas de la tabla de deudores
 * que aplica colores según el estado de pago
 */
public class DeudorCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor,
            boolean estaSeleccionado, boolean tieneFoco, int fila, int columna) {
        
        /* Declaración de todas las variables al inicio del método */
        Component componente;
        DeudorTableModel modelo;
        Deudores deudor;
        boolean estaPagado;
        
        componente = super.getTableCellRendererComponent(tabla, valor, estaSeleccionado, tieneFoco, fila, columna);
        modelo = (DeudorTableModel) tabla.getModel();
        deudor = modelo.getDeudorAt(fila);
        estaPagado = deudor.isPagado();

        /* Aplicar colores solo si NO está seleccionado */
        if (!estaSeleccionado) {
            
            if (estaPagado) {
                componente.setBackground(new Color(144, 238, 144)); // Verde claro
                componente.setForeground(Color.BLACK);
            } else {
                componente.setBackground(new Color(255, 182, 193)); // Rojo claro
                componente.setForeground(Color.BLACK);
            }
            
        } else {
            // Si está seleccionado, usar colores por defecto de selección
            componente.setBackground(tabla.getSelectionBackground());
            componente.setForeground(tabla.getSelectionForeground());
        }

        return componente;
    }
    
}