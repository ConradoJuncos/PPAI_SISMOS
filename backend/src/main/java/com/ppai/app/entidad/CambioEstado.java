package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CambioEstado {

    // Atributos
    private long idCambioEstado;
    private LocalDateTime fechaHoraFin;
    private LocalDateTime fechaHoraInicio;
    private Estado estado;
    private List<MotivoFueraServicio> motivoFueraServicio = new ArrayList<MotivoFueraServicio>();
    private Empleado responsableInspeccion;

    // Constructor sin parámetros
    public CambioEstado(){}

    // Constructor con parámetros minimos
    public CambioEstado(LocalDateTime fechaHoraActual, Estado estado, Empleado responsableInspeccion) {
        setFechaHoraInicio(fechaHoraActual);
        setEstado(estado);
        setResponsableInspeccion(responsableInspeccion);
    }

    // Comportamiento
    // Verficar si el cambio de estado es actual
    public boolean esEstadoActual() {
        if (this.fechaHoraFin == null) {
            return true;
        }
        return false;
    }
    
    // verificar si el estado asociado al cambio de estado es auto
    public boolean sosAutoDetectado(){

        // Preguntar al estado asociado si es auto detectado
        if (this.estado.sosAutoDetectado()){
            
            // El estado asociado es auto detectad
            return true;
        }

        // El estado asociado no es auto detectado
        return false;
    }

    // Métodos Getters y Setters
    public long getIdCambioEstado() {
        return this.idCambioEstado;
    }
    public LocalDateTime getFechaHoraFin(){
        return this.fechaHoraFin;
    }
    public LocalDateTime getFechaHoraInicio(){
        return this.fechaHoraInicio;
    }
    public Estado getEstado(){
        return this.estado;
    }
    public List<MotivoFueraServicio> getMotivoFueraServicio(){
        return this.motivoFueraServicio;
    }
    public Empleado getResponsableInspeccion(){
        return this.responsableInspeccion;
    }
    public void setIdCambioEstado(long idCambioEstado){
        this.idCambioEstado = idCambioEstado;
    }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin){
        this.fechaHoraFin = fechaHoraFin;
    }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio){
        this.fechaHoraInicio = fechaHoraInicio;
    }
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    public void setMotivoFueraServicio(List<MotivoFueraServicio> motivoFueraServicio) {
        this.motivoFueraServicio = motivoFueraServicio;
    }
    public void setResponsableInspeccion(Empleado responsableInspeccion){
        this.responsableInspeccion = responsableInspeccion;
    }
}
