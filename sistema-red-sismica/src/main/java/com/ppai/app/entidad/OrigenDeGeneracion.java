package com.ppai.app.entidad;

public class OrigenDeGeneracion {

    // Atributos
    private long idOrigenDeGeneracion; 
    private String descripcion;
    private String nombre;

    // Métodos getter y setter
    public long getOrigenDeGeneracion(){
        return this.idOrigenDeGeneracion;
    }
    public String getDescripcion(){
        return this.descripcion;
    }
    public String getNombre(){
        return this.nombre;
    }
    public void setIdOrigenDeGeneracion(long idOrigenDeGeneracion) {
        this.idOrigenDeGeneracion = idOrigenDeGeneracion;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
}
