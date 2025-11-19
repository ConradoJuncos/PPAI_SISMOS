package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class CambioEstado {

    // Atributos
    private long idCambioEstado;
    private LocalDateTime fechaHoraFin;
    private LocalDateTime fechaHoraInicio;
    private Estado estado;
    private Empleado responsableInspeccion;
    private long idEventoSismico;  // FK a EventoSismico

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
        return this.fechaHoraFin == null;
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
    public void setResponsableInspeccion(Empleado responsableInspeccion){
        this.responsableInspeccion = responsableInspeccion;
    }
    public long getIdEventoSismico() {
        return this.idEventoSismico;
    }
    public void setIdEventoSismico(long idEventoSismico) {
        this.idEventoSismico = idEventoSismico;
    }
}
