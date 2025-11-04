package com.ppai.app.entidad;

public class ModeloSismografo {
    
    // Atributos
    private long idModeloSismografo; 
    private String caracteristicas;
    private String nombreModelo;
    private long idFabricante;

    // Métodos Getter y Setter
    public long getIdModeloSismogafo(){
        return this.idModeloSismografo;
    }
    public String getCaracteristicas(){
        return this.caracteristicas;
    }
    public String getNombreModelo(){
        return this.nombreModelo;
    }
    public long getIdFabricante(){
        return this.idFabricante;
    }
    public void setIdModeloSismogrfo(long idModeloSismografo) {
        this.idModeloSismografo = idModeloSismografo;
    }
    public void setCaracteristicas(String caracteristicas){
        this.caracteristicas = caracteristicas;
    }
    public void setNombreModelo(String nombreModelo){
        this.nombreModelo = nombreModelo;
    }
    public void setIdFabricante(long idFabricante){
        this.idFabricante = idFabricante;
    }


}
