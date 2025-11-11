package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TareaAsignada {

    // Atributos
    private long idTareaAsignada;
    private String comentario;
    private LocalDateTime fechaHoraRealizacion;
    private TipoTareaInspeccion tarea;
    private List<ApreciacionTipo> apreciacion = new ArrayList<ApreciacionTipo>();

    // Método constructor sin parámetros
    public TareaAsignada(){}

    // Métodos Getter y Setter
    public long getIdTareaAsignada(){
        return this.idTareaAsignada;
    }
    public String getComentario(){
        return this.comentario;
    }
    public LocalDateTime getFechaHoraRealizacion(){
        return this.fechaHoraRealizacion;
    }
    public TipoTareaInspeccion getTarea(){
        return this.tarea;
    }
    public List<ApreciacionTipo> getApreciacion(){
        return this.apreciacion;
    }
    public void setIdTareaAsignada(long idTareaAsignada){
        this.idTareaAsignada = idTareaAsignada;
    }
    public void setTipoTareaInspeccion(TipoTareaInspeccion tarea){
        this.tarea = tarea;
    }
    public void setComentario(String comentario){
        this.comentario = comentario;
    }
    public void setFechaHoraRealizacion(LocalDateTime fechaHoraRealizacion){
        this.fechaHoraRealizacion = fechaHoraRealizacion;
    }
    public void setTarea(TipoTareaInspeccion tarea){
        this.tarea = tarea;
    }
    public void setApreciacion(List<ApreciacionTipo> apreciacion){
        this.apreciacion = apreciacion;
    }
}
