package com.ppai.app.entidad;

public class Fabricante {
 
    // Atributos
    private long idFabricante;
    private String nombre;
    private String razonSocial;

    // Método Getter y Setter
    public long getIdFabricante(){
        return this.idFabricante;
    }
    public String getNombre(){
        return this.nombre;
    }
    public String getRazonSocial(){
        return this.razonSocial;
    }
    public void setIdFabricante(long idFabricante){
        this.idFabricante = idFabricante;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setRazonSocial(String razonSocial){
        this.razonSocial = razonSocial;
    }
}
