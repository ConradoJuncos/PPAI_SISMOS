package com.ppai.app.entidad;

public class AlcanceSismo {
    
    private long idAlcanceSismo;
    private String descripcion;
    private String nombre;

    public long getIdAlcanceSismo(){
        return this.idAlcanceSismo;
    }
    public String getNombre(){
        return this.nombre;
    }
    public String getDescripcion(){
        return this.descripcion;
    }
    public void setIdAlcanceSismo(long idAlcanceSismo){
        this.idAlcanceSismo = idAlcanceSismo;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
}
