package app.modelo;

import java.util.Objects;

/*
* Clase DetalleVenta 
*/
public class DetalleVenta {
    
    private Producto Producto;
    private int Cantidad;
    private double PrecioUnitario;

    /*Contructor vacio*/
    public DetalleVenta() {}

    /*Constructor
    * @param producto
    * @param cantidad
    * @param precioUnitario
    */ 
   public DetalleVenta(Producto producto, int cantidad, double precioUnitario) {
        this.Producto = producto;
        this.Cantidad = cantidad;
        this.PrecioUnitario = precioUnitario;
    }

    /*
    * Getters y Setters
    */
    public Producto getProducto() {
        return Producto;
    }

    public void setProducto(Producto Producto) {
        this.Producto = Producto;
    }

    public int getCantidad() {
        return Cantidad;
    }

   public void setCantidad(int Cantidad) {
        if (Cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        this.Cantidad = Cantidad;
   }

    public double getPrecioUnitario() {
        return PrecioUnitario;
    }

    public void setPrecioUnitario(double PrecioUnitario) {
        if (PrecioUnitario <= 0) {
            throw new IllegalArgumentException("El precio debe ser positivo");
        }
        this.PrecioUnitario = PrecioUnitario;
    }

    /*Total parcial (Por producto) */
    public double getTotal() {
        return PrecioUnitario * Cantidad;
    }

    @Override
    public String toString() {
        return String.format("Producto: %s, Cantidad: %d, Total: $%.2f",
                    Producto.getNombre(), Cantidad, getTotal());
    }

    // Métodos equals y hashCode para comparaciones
    @Override
    public boolean equals(Object o) {
        if (this == o) 
            return true;
        if (o == null || getClass() != o.getClass()) 
            return false;
        DetalleVenta DVenta = (DetalleVenta) o;
        return Cantidad == DVenta.Cantidad &&
            Double.compare(DVenta.PrecioUnitario, PrecioUnitario) == 0 &&
            Objects.equals(Producto, DVenta.Producto);
        }   

        @Override
        public int hashCode() {
            return Objects.hash(Producto, Cantidad, PrecioUnitario);
        }
}
