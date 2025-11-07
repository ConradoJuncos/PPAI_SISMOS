package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class ReclamoGarantia {

    // Atributos 
    private long nroReclamo;
    private String comentario;
    private LocalDateTime fechaReclamo;
    private LocalDateTime fechaRespuesta;
    private String respuestaFabricante;
    private Sismografo sismografo;
    private Fabricante fabricante;

    // Métodos Getter y Setter
    public long getNroReclamo(){
        return this.nroReclamo;
    }
    public String getComentario(){
        return this.comentario;
    }
    public LocalDateTime getFechaReclamo(){
        return this.fechaReclamo;
    }
    public LocalDateTime getFechaRespuesta(){
        return this.fechaRespuesta;
    }
    public String getRespuestaFabricante(){
        return this.respuestaFabricante;
    }
    public Sismografo getSismografo(){
        return this.sismografo;
    }
    public Fabricante getFabricante(){
        return this.fabricante;
    }
    public void setNroReclamo(long nroReclamo){
        this.nroReclamo = nroReclamo;
    }
    public void setComentario(String comentario){
        this.comentario = comentario;
    }
    public void setFechaReclamo(LocalDateTime fechaReclamo){
        this.fechaReclamo = fechaReclamo;
    }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta){
        this.fechaReclamo = fechaRespuesta;
    }
    public void setRespuestaFabricante(String respuestaFabricante){
        this.respuestaFabricante = respuestaFabricante;
    }
    public void setSismografo(Sismografo sismografo){
        this.sismografo = sismografo;
    }
    public void setFabricante(Fabricante fabricante){
        this.fabricante = fabricante;
    }
    
}
