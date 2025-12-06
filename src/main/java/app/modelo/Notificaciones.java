package app.modelo;

import java.util.Date;

public class Notificaciones {

    private int id;
    private int productoId;
    private String productoNombre;
    private int cantidadActual;
    private int cantidadMinima;
    private Date fechaCreacion;
    private boolean revisada;

    // Constructor vacío
    public Notificaciones() {
    }

    // Constructor con todos los campos
    public Notificaciones(int productoId, String productoNombre, int cantidadActual) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.cantidadActual = cantidadActual;
        this.cantidadMinima = 5;
        this.revisada = false;
        this.fechaCreacion = new Date();
    }

    // Getters y Setters
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public int getProductoId() { 
        return productoId; 
    }
    
    public void setProductoId(int productoId) { 
        this.productoId = productoId; 
    }

    public String getProductoNombre() { 
        return productoNombre; 
    }
    
    public void setProductoNombre(String productoNombre) { 
        this.productoNombre = productoNombre; 
    }

    public int getCantidadActual() { 
        return cantidadActual; 
    }
    
    public void setCantidadActual(int cantidadActual) { 
        this.cantidadActual = cantidadActual; 
    }

    public int getCantidadMinima() { 
        return cantidadMinima; 
    }
    
    public void setCantidadMinima(int cantidadMinima) { 
        this.cantidadMinima = cantidadMinima; 
    }

    public Date getFechaCreacion() { 
        return fechaCreacion; 
    }
    
    public void setFechaCreacion(Date fechaCreacion) { 
        this.fechaCreacion = fechaCreacion; 
    }

    public boolean isRevisada() { 
        return revisada; 
    }
    
    public void setRevisada(boolean revisada) { 
        this.revisada = revisada; 
    }
}

