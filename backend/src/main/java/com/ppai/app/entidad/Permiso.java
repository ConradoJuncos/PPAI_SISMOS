package com.ppai.app.entidad;

public class Permiso {

    // Atributos
    private long idPermiso;
    private String descripcion;
    private String nombre;

    // Métodos Getter y Setter
    public long getIdPermiso(){
        return this.idPermiso;
    }
    public String getDescripcion(){
        return this.descripcion;
    }
    public String getNombre(){
        return this.nombre;
    }
    public void setIdPermiso(long idPermiso){
        this.idPermiso = idPermiso;
    }
    public void setDescripcion (String descripcion){
        this.descripcion = descripcion;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
}
