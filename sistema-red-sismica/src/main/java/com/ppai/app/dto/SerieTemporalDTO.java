package com.ppai.app.dto;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class SerieTemporalDTO {
    
    // Atributos 
    private String nombreEstacion; // atributo para la informacion sismica
    private long codigoEstacion; // atributo para la inforamcion sismica
    private long idSerieTemporal;
    private LocalDateTime fechaHoraRegistro;
    private double frecuenciaMuestreo;
    private List<MuestraSismicaDTO> muestras = new ArrayList<MuestraSismicaDTO>();

    // Método Constructor con Parámetros inciales
    public SerieTemporalDTO(long idSerieTemporal, LocalDateTime fechaHoraRegistro, 
    double frecuenciaMuestreo, List<MuestraSismicaDTO> muestras){
        this.idSerieTemporal = idSerieTemporal;
        this.fechaHoraRegistro = fechaHoraRegistro;
        this.frecuenciaMuestreo = frecuenciaMuestreo;
        this.muestras = muestras;
    }

    // Métodos Getter y Setter
    public String getNombreEstacion(){
        return this.nombreEstacion;
    }
    public long getCodigoEstacion(){
        return this.codigoEstacion;
    }
    public long getIdSerieTemporal(){
        return this.idSerieTemporal;
    }
    public LocalDateTime getFechaHoraRegistro(){
        return this.fechaHoraRegistro;
    }
    public double getFrecuenciaMuestreo(){
        return this.frecuenciaMuestreo;
    }
    public List<MuestraSismicaDTO> getMuestras(){
        return this.muestras;
    }
    
    public void setNombreEstacion(String nombreEstacion){
        this.nombreEstacion = nombreEstacion;
    }
    public void setCodigoEstacion(long codigoEstacion){
        this.codigoEstacion = codigoEstacion;
    }
    public void setIdSerieTemporal(long idSerieTemporal){
        this.idSerieTemporal = idSerieTemporal;
    }
    public void setFechaHoraRegistro(LocalDateTime fechaHoraRegistro){
        this.fechaHoraRegistro = fechaHoraRegistro;
    }
    public void setFrecuenciaMuestreo(double frecuenciaMuestreo){
        this.frecuenciaMuestreo = frecuenciaMuestreo;
    }
    public void setMuestras(List<MuestraSismicaDTO> muestras){
        this.muestras = muestras;
    }
    
}
