package com.ppai.app.dto;

import java.time.LocalDateTime;

public class MuestraSismicaDTO {

    // Atributos
    private LocalDateTime fechaHora;
    private double velocidadOnda;
    private double frecuenciaOnda;
    private double longitudOnda;

    // Constructor sin parámetros
    public MuestraSismicaDTO(){}

    // Constructor con parametros
    public MuestraSismicaDTO(LocalDateTime fechaHora, double velocidadOnda, double frecuenciaOnda, double longitudOnda){
        this.fechaHora = fechaHora;
        this.velocidadOnda = velocidadOnda;
        this.frecuenciaOnda = frecuenciaOnda;
        this.longitudOnda = longitudOnda;
    }

    // Métodos Getter y Setter
    public LocalDateTime getFechaHora(){
        return this.fechaHora;
    }
    public double getVelocidadOnda(){
        return this.velocidadOnda;
    }
    public double getFrecuenciaOnda(){
        return this.frecuenciaOnda;
    }
    public double getLongitudOnda(){
        return this.longitudOnda;
    }

    public void setFechaHora(LocalDateTime fechaHora){
        this.fechaHora = fechaHora;
    }
    public void setVelocidadOnda(double velocidadOnda){
        this.velocidadOnda = velocidadOnda;
    }
    public void setFrecuenciaOnda(double frecuenciaOnda){
        this.frecuenciaOnda = frecuenciaOnda;
    }
    public void setLongitudOnda(double longitudOnda){
        this.longitudOnda = longitudOnda;
    }

    
}
