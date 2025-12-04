/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.modelo;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author omarf
 */
public class Venta {
    private int id;
    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    private LocalDateTime fecha;

    /*Contructor default */
    public Venta() {}
    
    // Constructor completo
    public Venta(int id, Producto producto, int cantidad, double precioUnitario, LocalDateTime fecha) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fecha = fecha;
    }

    // Constructor simplificado para nuevas ventas
    public Venta(Producto producto, int cantidad) {
        this(0, producto, cantidad, producto.getPrecio(), LocalDateTime.now());
    }

    // Método para calcular el total de la venta
    public double getTotal() {
        return cantidad * precioUnitario;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        if (precioUnitario <= 0) {
            throw new IllegalArgumentException("El precio debe ser positivo");
        }
        this.precioUnitario = precioUnitario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula");
    }

    @Override
    public String toString() {
        return String.format("Venta [ID: %d, Producto: %s, Cantidad: %d, Total: $%.2f, Fecha: %s]",
                id, producto.getNombre(), cantidad, getTotal(), fecha);
    }

    // Métodos equals y hashCode para comparaciones
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venta venta = (Venta) o;
        return id == venta.id &&
                cantidad == venta.cantidad &&
                Double.compare(venta.precioUnitario, precioUnitario) == 0 &&
                Objects.equals(producto, venta.producto) &&
                Objects.equals(fecha, venta.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, producto, cantidad, precioUnitario, fecha);
    }
}
