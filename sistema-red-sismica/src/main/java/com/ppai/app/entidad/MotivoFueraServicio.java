package com.ppai.app.entidad;

public class MotivoFueraServicio {
 
    // Atributos
    private long idMotivoFueraServicio;
    private String comentario;
    private MotivoTipo motivoTipo;

    // Métodos Getter y Setter
    public long getIdMotivoFueraServicio(){
        return this.idMotivoFueraServicio;
    }
    public String getComentario(){
        return this.comentario;
    }
    public MotivoTipo getMotivoTipo(){
        return this.motivoTipo;
    }
    public void setIdMotivoFueraServicio(long idMotivoFueraServicio){
        this.idMotivoFueraServicio = idMotivoFueraServicio;
    }
    public void setComentario(String comentario){
        this.comentario = comentario;
    }
    public void setMotivoTipo(MotivoTipo motivoTipo){
        this.motivoTipo = motivoTipo;
    }
}
