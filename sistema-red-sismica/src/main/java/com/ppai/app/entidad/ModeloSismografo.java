package com.ppai.app.entidad;

public class ModeloSismografo {
    
    // Atributos
    private long idModeloSismografo; 
    private String caracteristicas;
    private String nombreModelo;

    // Métodos Getter y Setter
    public long getIdModeloSismografo(){
        return this.idModeloSismografo;
    }
    public String getCaracteristicas(){
        return this.caracteristicas;
    }
    public String getNombreModelo(){
        return this.nombreModelo;
    }
    public void setIdModeloSismografo(long idModeloSismografo) {
        this.idModeloSismografo = idModeloSismografo;
    }
    public void setCaracteristicas(String caracteristicas){
        this.caracteristicas = caracteristicas;
    }
    public void setNombre(String nombreModelo){
        this.nombreModelo = nombreModelo;
    }
}
