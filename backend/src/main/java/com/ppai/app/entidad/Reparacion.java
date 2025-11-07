package com.ppai.app.entidad;

import java.time.LocalDateTime;;

public class Reparacion {
    
    // Atributos
    private int nroReparacion;
    private String comentarioReparacion;
    private String comentarioSolucion;
    private LocalDateTime fechaEnvioReparacion;
    private LocalDateTime fechaRespuestaReparacion;

    // Métodos Getter y Setter
    public int getNroReparacion(){
        return this.nroReparacion;
    }
    public String getComentarioReparacion(){
        return this.comentarioReparacion;
    }
    public String getComentarioSolucion(){
        return this.comentarioSolucion;
    }
    public LocalDateTime getFechaEnvioReparacion(){
        return this.fechaEnvioReparacion;
    }
    public LocalDateTime getFechaRespuestaReparacion(){
        return this.fechaRespuestaReparacion;
    }
    public void setNroReparacion(int nroReparacion){
        this.nroReparacion = nroReparacion;
    }
    public void setComentarioReparacion(String comentarioReparacion){
        this.comentarioReparacion = comentarioReparacion;
    }
    public void setComentarioSolucion(String comentarioSolucion){
        this.comentarioSolucion = comentarioSolucion;
    }
    public void setFechaEnvioReparacion(LocalDateTime fechaEnvioReparacion){
        this.fechaEnvioReparacion = fechaEnvioReparacion;
    }
    public void setFechaRespuestaReparacion(LocalDateTime fechaRespuestaRepracion){
        this.fechaRespuestaReparacion = fechaRespuestaRepracion;
    }
}
