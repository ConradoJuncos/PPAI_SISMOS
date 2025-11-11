package com.ppai.app.entidad;

public class Rol {

    // Atributos
    private long idRol;
    private String nombre;
    private String descripcion;
    
    // Getters y Setters
    public long getIdRol(){
        return this.idRol;
    }
    public String getNombre(){
        return this.nombre;
    }
    public String getDescripcion(){
        return this.descripcion;
    }
    public void setIdRol(long idRol){
        this.idRol = idRol;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
    
}
