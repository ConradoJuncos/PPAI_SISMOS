package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ppai.app.dto.SerieTemporalDTO;
import com.ppai.app.entidad.MuestraSismica;

public class SerieTemporal {
    
    // Atributos
    private long idSerieTemporal;
    private String condicionAlarma;
    private LocalDateTime fechaHoraRegistro;
    private String frecuenciaMuestreo;
    private Estado estado; 
    private List<MuestraSismica> muestrasSismicas = new ArrayList<>();

    // Constructor sin parámetros
    public SerieTemporal(){}

    public ArrayList<Object> getDatos() {
        return recolectarInformacionSerieTemporal();
    }

    // Recolectar informacion sismica de la serie temporal
    public ArrayList<Object> recolectarInformacionSerieTemporal(){

        ArrayList<Object> informacionSerieTemporal = new ArrayList<Object>();
        informacionSerieTemporal.add(this.getIdSerieTemporal());
        informacionSerieTemporal.add(this.getFechaHoraRegistro());
        informacionSerieTemporal.add(this.getFrecuenciaMuestreo());

        return informacionSerieTemporal;

    }

    // Recolectar la informacion de las muestras sismicas asociadas a la serie temopral
    public ArrayList<Object> recolectarInformacionMuestrasSismicas(Long idSerieTemporal, LocalDateTime fechaHoraRegistro, String frecuenciaMuestreo){

        ArrayList<Object> informacionMuestras = new ArrayList<Object>();
        for (MuestraSismica muestra : muestrasSismicas){
            informacionMuestras.add(muestra.getDatos());
        }

        return informacionMuestras;
    }

    // Métodos Getter y Setter
    public long getIdSerieTemporal(){
        return this.idSerieTemporal;
    }
    public String getCondicionAlarma(){
        return this.condicionAlarma;
    }
    public LocalDateTime getFechaHoraRegistro(){
        return this.fechaHoraRegistro;
    }
    public String getFrecuenciaMuestreo(){
        return this.frecuenciaMuestreo;
    }
    public Estado getEstado(){
        return this.estado;
    }
    public List<MuestraSismica> getMuestrasSismicas(){
        return this.muestrasSismicas;
    }
    public void setIdSerieTemporal(long idSerieTemporal){
        this.idSerieTemporal = idSerieTemporal;
    }
    public void setCondicionAlarma(String condicionAlarma) {
        this.condicionAlarma = condicionAlarma;
    }
    public void setFechaHoraRegistro(LocalDateTime fechaHoraRegistro){
        this.fechaHoraRegistro = fechaHoraRegistro;
    }
    public void setFrecuenciaMuestreo(String frecuenciaMuestreo){
        this.frecuenciaMuestreo = frecuenciaMuestreo;
    }
    public void setEstado(Estado estado){
        this.estado = estado;
    }
    public void setMuestrasSismicas(List<MuestraSismica> muestrasSismicas) {
        this.muestrasSismicas = muestrasSismicas;
    }
}
