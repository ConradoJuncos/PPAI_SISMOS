package com.ppai.app.entidad;

public class TipoTareaInspeccion {
    
    // Atributos
    private long codigo;
    private String descripcionTrabajo;
    private String duracionEstimada;
    private String nombre;

    // Método Constructor sin parámetros
    public TipoTareaInspeccion(){}

    // Métodos Getter y Setter
    public long getCodigo(){
        return this.codigo;
    }
    public String getDescripcionTrabajo(){
        return this.descripcionTrabajo;
    }
    public String getDuracionEstimada(){
        return this.duracionEstimada;
    }
    public String getNombre(){
        return this.nombre;
    }
    public void setCodigo(long codigo){
        this.codigo = codigo;
    }
    public void setDescripcionTrabajo(String descripcionTrabajo){
        this.descripcionTrabajo = descripcionTrabajo;
    }
    public void setDuracionEstimada(String duracionEstimada){
        this.duracionEstimada = duracionEstimada;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
}
