package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class TrabajoARealizar {

    // Atributos
    private long idTrabajoARealizar;
    private String comentario;
    private LocalDateTime fechaFinPrevista;
    private LocalDateTime fechaFinReal;
    private LocalDateTime fechaInicioPrevista;
    private LocalDateTime fechaInicioReal;
    private TipoTrabajo definicionTrabajo;

    // Comportamiento
    public boolean esFinalizado(){
        if (this.fechaFinReal == null){
            return false;
        }
        return true;
    }

    // Método Getter y Setter
    public long getIdTrabajoARealizar(){
        return this.idTrabajoARealizar;
    }
    public String getComentario(){
        return this.comentario;
    }
    public LocalDateTime getFechaFinPrevista(){
        return this.fechaFinPrevista;
    }
    public LocalDateTime getFechaFinReal(){
        return this.fechaFinReal;
    }
    public LocalDateTime getFechaInicioPrevista(){
        return this.fechaInicioPrevista;
    }
    public LocalDateTime getFechaInicioReal(){
        return this.fechaInicioReal;
    }
    public TipoTrabajo getDefinicionTrabajo(){
        return this.definicionTrabajo;
    }
    public void setIdTrabajoARealizar(long idTrabajoARealizar){
        this.idTrabajoARealizar = idTrabajoARealizar;
    }
    public void setComentario(String comentario){
        this.comentario = comentario;
    }
    public void setFechaFinPrevista(LocalDateTime fechaFinPrevista){
        this.fechaFinPrevista = fechaFinPrevista;
    }
    public void setFechaFinReal(LocalDateTime fechaFinReal){
        this.fechaFinReal = fechaFinReal;
    }
    public void setFechaInicioPrevista(LocalDateTime fechaInicioPrevista){
        this.fechaInicioPrevista = fechaInicioPrevista;
    }
    public void setFechaInicioReal(LocalDateTime fechaInicioReal){
        this.fechaInicioReal = fechaInicioReal;
    }
    public void setDefinicionTrabajo(TipoTrabajo definicionTrabajo){
        this.definicionTrabajo = definicionTrabajo;
    }
    
}
