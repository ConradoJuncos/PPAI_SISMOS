package com.ppai.app.entidad;

/**
 * Clase base de ejemplo para tus entidades de dominio.
 * Elimina esta clase y crea las entidades que necesites para tu proyecto.
 */
public class EntidadEjemplo {
    
    private Long id;
    private String nombre;
    
    public EntidadEjemplo() {
    }
    
    public EntidadEjemplo(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
