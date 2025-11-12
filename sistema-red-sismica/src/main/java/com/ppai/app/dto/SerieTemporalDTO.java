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
    private String frecuenciaMuestreo;
    private List<MuestraSismicaDTO> muestras = new ArrayList<MuestraSismicaDTO>();

    // Método Constructor sin parametros
    public SerieTemporalDTO(){}

    // Método Constructor con Parámetros inciales
    public SerieTemporalDTO(long idSerieTemporal, LocalDateTime fechaHoraRegistro, 
    String frecuenciaMuestreo){
        this.idSerieTemporal = idSerieTemporal;
        this.fechaHoraRegistro = fechaHoraRegistro;
        this.frecuenciaMuestreo = frecuenciaMuestreo;
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
    public String getFrecuenciaMuestreo(){
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
    public void setFrecuenciaMuestreo(String frecuenciaMuestreo){
        this.frecuenciaMuestreo = frecuenciaMuestreo;
    }
    public void setMuestras(List<MuestraSismicaDTO> muestras){
        this.muestras = muestras;
    }
    // Para setear de a una muestra
    public void setMuestra(MuestraSismicaDTO muestra) {
        this.muestras.add(muestra);
    }
    
}
