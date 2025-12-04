<<<<<<< HEAD
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
=======
    /*
    * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
    * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
    */
    package app.modelo;
    import java.time.LocalDateTime;
    import java.util.Objects;
    import java.util.List;
    import java.util.ArrayList;

    /**
     *
     * @author omarf
     */
    public class Venta {
        private int id;
        private LocalDateTime fecha;
        private List<DetalleVenta> VentaDetalles;

        /*Contructor default */
        public Venta() {
            this.VentaDetalles = new ArrayList<>();
            this.fecha = LocalDateTime.now();
        }
        
        // Constructor completo
        public Venta(int id, LocalDateTime fecha) {
            VentaDetalles = new ArrayList<>();
            this.id = id;
            this.fecha = fecha;
        }

        // Constructor simplificado para nuevas ventas
        /* 
        public Venta(Producto producto, int cantidad) {
            this(0, producto, cantidad, producto.getPrecio(), LocalDateTime.now());
        }*/

        // Método para calcular el total de la venta
        public double getTotal() {
            double Total = 0.0;
            for(DetalleVenta Detalle : VentaDetalles) {
                Total += Detalle.getTotal();
            }
            return Total;
        }
        
        // Método para agregar un detalle de venta
        public void AgregarDetalle(DetalleVenta detalle) {
            this.VentaDetalles.add(Objects.requireNonNull(detalle, "El detalle no puede ser nulo"));
        }

        // Getters y Setters
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
            this.VentaDetalles = Objects.requireNonNull(VentaDetalles, "La lista de detalles no puede ser nula");
        }

        public LocalDateTime getFecha() {
            return fecha;
        }

        public void setFecha(LocalDateTime fecha) {
            this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula");
        }

        @Override
        public String toString() {
            return String.format("Venta [ID: %d, Fecha: %s, Total: $%.2f, Detalles: %s]",
                    id, fecha, getTotal(), VentaDetalles.toString());
        }

        // Métodos equals y hashCode para comparaciones
        @Override
        public boolean equals(Object o) {
            if (this == o) 
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
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
>>>>>>> HU-15
