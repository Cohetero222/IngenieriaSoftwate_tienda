package modelo;

public class Deudores {

    private int id;
    private String nombre;
    private String producto;
    private String marca;
    private String categoria;
    private int cantidad;
    private double precio;
    private double costo;
    private boolean pagado;

    // Constructor vacío
    public Deudores() {
    }

    // Constructor con todos los campos menos id (porque es autoincrement)
    public Deudores(String nombre, String producto, String marca,
            String categoria, int cantidad, double precio, double costo, boolean pagado) {
        this.nombre = nombre;
        this.producto = producto;
        this.marca = marca;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.precio = precio;
        this.costo = costo;
        this.pagado = pagado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isPagado() {
    return pagado;
}

public void setPagado(boolean pagado) {
    this.pagado = pagado;
}

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

     //Metodo para mostrar "Si"/"No" en la tabla
    public String getPagadoAsString() {
        return pagado ? "Si" : "No";
    }
    
    //Metodo para convertir de String a boolean
    public static boolean parsePagado(String texto) {
        return "Si".equalsIgnoreCase(texto) || "Sí".equalsIgnoreCase(texto) || "true".equalsIgnoreCase(texto);
    }
}