package com.ppai.app.entidad;

public class MagnitudRichter {

    // atributos
    private String descripcionMagnitud;
    private int numero; 

    // métodos getter y setter
    public int getNumero() {
        return this.numero;
    }

    public String getDescripcionMagnitud(){
        return this.descripcionMagnitud;
    }
    public void setNumero(int numero){
        this.numero = numero;
    }
    public void setDescripcionMagnitud(String descripcionMagnitud){
        this.descripcionMagnitud = descripcionMagnitud;
    }
    
}
