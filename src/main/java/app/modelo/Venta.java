package app.modelo;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

public class Venta {
    private int id;
    private LocalDateTime fecha;
    private List<DetalleVenta> VentaDetalles;

    public Venta() {
        this.VentaDetalles = new ArrayList<>();
        this.fecha = LocalDateTime.now();
    }

    public Venta(int id, LocalDateTime fecha) {
        this.VentaDetalles = new ArrayList<>();
        this.id = id;
        this.fecha = fecha;
    }

    public double getTotal() {
        double total = 0.0;
        for (DetalleVenta detalle : VentaDetalles) {
            total += detalle.getTotal();
        }
        return total;
    }

    public void AgregarDetalle(DetalleVenta detalle) {
        this.VentaDetalles.add(Objects.requireNonNull(detalle));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DetalleVenta> getVentaDetalles() {
        return VentaDetalles;
    }

    public void setVentaDetalles(List<DetalleVenta> VentaDetalles) {
        this.VentaDetalles = Objects.requireNonNull(VentaDetalles);
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = Objects.requireNonNull(fecha);
    }

    @Override
    public String toString() {
        return String.format("Venta [ID: %d, Fecha: %s, Total: %.2f, Detalles: %s]",
                id, fecha, getTotal(), VentaDetalles.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venta venta = (Venta) o;
        return id == venta.id &&
                Objects.equals(fecha, venta.fecha) &&
                Objects.equals(VentaDetalles, venta.VentaDetalles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fecha, VentaDetalles);
    }
}