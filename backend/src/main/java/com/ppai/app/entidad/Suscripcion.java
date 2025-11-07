package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Suscripcion {
    
    // Atributos
    private long idSuscripcion;
    private LocalDateTime fechaHoraFinSuscripcion;
    private LocalDateTime fechaHoraInicioSuscripcion;
    private List<EstacionSismologica> estacionSismologica = new ArrayList<EstacionSismologica>();

    // Comportamiento
    public boolean esVigente(){
        if (this.fechaHoraFinSuscripcion == null){
            // Es vigente
            return true;
        }
        // No es vigente
        return false;
    }

    // Métodos Getter y Setter
    public long getIdSuscripcion(){
        return this.idSuscripcion;
    }
    public LocalDateTime getFechaHoraFinSuscripcion(){
        return this.fechaHoraFinSuscripcion;
    }
    public LocalDateTime getFechaHoraInicioSuscripcion(){
        return this.fechaHoraInicioSuscripcion;
    }
    public List<EstacionSismologica> getEstacionSismologica(){
        return this.estacionSismologica;
    }
    public void setIdSuscripcion(long idSuscripcion){
        this.idSuscripcion = idSuscripcion;
    }
    public void setFechaHoraFinSuscripcion(LocalDateTime fechaHoraFinSuscripcion){
        this.fechaHoraFinSuscripcion = fechaHoraFinSuscripcion;
    }
    public void setFechaHoraInicioSuscripcion(LocalDateTime fechaHoraInicioSuscripcion){
        this.fechaHoraInicioSuscripcion = fechaHoraInicioSuscripcion;
    }
    public void setEstacionSismologica(List<EstacionSismologica> estacionSismologica){
        this.estacionSismologica = estacionSismologica;
    }


}
