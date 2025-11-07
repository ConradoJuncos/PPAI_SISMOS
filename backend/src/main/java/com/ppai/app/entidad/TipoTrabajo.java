package com.ppai.app.entidad;

public class TipoTrabajo {

    // Atributos
    private long idTipoTrabajo;
    private String descripcion;
    private String nombre;

    // Métodos Getter y Setter
    public long getIdTipoTrabajo(){
        return this.idTipoTrabajo;
    }
    public String getDescripcion(){
        return this.descripcion;
    }
    public String getNombre(){
        return this.nombre;
    }
    public void setIdTipoTrabajo(long idTipoTrabajo){
        this.idTipoTrabajo = idTipoTrabajo;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
}
