package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenDeInspeccion {

    // Atributos
    private long numeroOrden;
    private LocalDateTime fechaHoraCierre;
    private LocalDateTime fechaHoraFinalizacion;
    private LocalDateTime fechaHoraInicio;
    private String observacionCierre;
    private Empleado empleado;
    private Estado estado;
    private List<TareaAsignada> tareaAsignada = new ArrayList<TareaAsignada>();
    private EstacionSismologica estacionSismologica;

    // Métodos Getter y Setter
    public long getNumeroOrden(){
        return this.numeroOrden;
    }
    public LocalDateTime getFechaHoraCierre(){
        return this.fechaHoraCierre;
    }
    public LocalDateTime getFechaHoraFinalizacion(){
        return this.fechaHoraFinalizacion;
    }
    public LocalDateTime getFechaHoraInicio(){
        return this.fechaHoraInicio;
    }
    public String getObservacionCierre(){
        return this.observacionCierre;
    }
    public Empleado getEmpleado(){
        return this.empleado;
    }
    public Estado getEstado(){
        return this.estado;
    }
    public List<TareaAsignada> getTareaAsignada(){
        return this.tareaAsignada;
    }
    public EstacionSismologica getEstacionSismologica(){
        return this.estacionSismologica;
    }
    public void estNumeroOden(long numeroOrden){
        this.numeroOrden = numeroOrden;
    }
    public void setFechaHoraCierre(LocalDateTime fechaHoraCierre){
        this.fechaHoraCierre = fechaHoraCierre;
    }
    public void setFechaHoraFinalizacion(LocalDateTime fechaHoraFinalizacion){
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
    }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio){
        this.fechaHoraInicio = fechaHoraInicio;
    }
    public void setObservacionCierre(String observacionCierre){
        this.observacionCierre = observacionCierre;
    }
    public void setEmpleado(Empleado empleado){
        this.empleado = empleado;
    }
    public void setTareaAsignada(List<TareaAsignada> tareaAsignada){
        this.tareaAsignada = tareaAsignada;
    }
    public void setEstacionSismologica(EstacionSismologica estacionSismologica){
        this.estacionSismologica = estacionSismologica;
    }
}
