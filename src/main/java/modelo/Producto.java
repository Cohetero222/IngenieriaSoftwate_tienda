/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;

/**
 *
 * @author omarf
 */
public class Producto {
    private int id;
    private String nombre;
    private String marca;
    private String categoria;
    private int cantidad;
    private String estado;
    private int ventas;
    private LocalDate fechaCaducidad;
    private double precio;
    private double costo;

    // Constructores
    public Producto() {
    }

    public Producto(String nombre, String marca, String categoria, int cantidad,
            String estado, int ventas, LocalDate fechaCaducidad, double precio, double costo) {
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.estado = estado;
        this.ventas = ventas;
        this.fechaCaducidad = fechaCaducidad;
        this.precio = precio;
        this.costo = costo;
    }

    // Métodos de negocio
    public boolean estaCaducado() {
        return fechaCaducidad != null && LocalDate.now().isAfter(fechaCaducidad);
    }

    public void registrarVenta(int cantidad) throws IllegalStateException {
        if (cantidad <= 0)
            throw new IllegalArgumentException("Cantidad debe ser positiva");
        if (cantidad > this.cantidad)
            throw new IllegalStateException("Stock insuficiente");

        this.cantidad -= cantidad;
        this.ventas += cantidad;
        actualizarEstado();
    }

    private void actualizarEstado() {
        if (estaCaducado()) {
            this.estado = "Caducado";
        } else if (this.cantidad == 0) {
            this.estado = "Agotado";
        } else {
            this.estado = "Disponible";
        }
    }

    // Getters & Setters (generados automáticamente en NetBeans con Alt + Insert)
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getVentas() {
        return ventas;
    }

    public void setVentas(int ventas) {
        this.ventas = ventas;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }
}
