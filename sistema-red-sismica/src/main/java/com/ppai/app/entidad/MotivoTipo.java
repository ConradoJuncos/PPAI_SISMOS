package com.ppai.app.entidad;

public class MotivoTipo {
    
    // Atributos
    private long idMotivoTipo;
    private String descripcion;
    
    // Métodos Getter y Setter
    public long getIdMotivoTipo(){
        return this.idMotivoTipo;
    }
    public String getDescripcion(){
        return this.descripcion;
    }
    public void setIdMotivoTipo(long idMotivoTipo){
        this.idMotivoTipo = idMotivoTipo;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
}
