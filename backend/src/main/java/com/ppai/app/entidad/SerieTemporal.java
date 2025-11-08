package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SerieTemporal {
    
    // Atributos
    private long idSerieTemporal;
    private String condicionAlarma;
    private LocalDateTime fechaHoraRegistro;
    private String frecuenciaMuestreo;
    private long idEstado; 
    private List<Long> idMuestraSismica = new ArrayList<>();

    // Constructor sin parámetros
    public SerieTemporal(){}

    // Comportamiento
    public String getDatos(){
        // Implementar la lógica de este método
        return "El método para obtener los datos de la Serie Temporal aun no ha sido implementado.";
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
    public long getIdEstado(){
        return this.idEstado;
    }
    public List<Long> getIdMuestraSismica(){
        return this.idMuestraSismica;
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
    public void setIdEstado(long idEstado){
        this.idEstado = idEstado;
    }
    public void setIdMuestraSismica(List<Long> idMuestraSismica) {
        this.idMuestraSismica = idMuestraSismica;
    }
}
