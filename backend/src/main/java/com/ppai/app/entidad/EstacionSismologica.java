package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class EstacionSismologica {

    // Atributos
    private long codigoEstacion;
    private String documentoCertificacionAdq;
    private LocalDateTime fechaSolicitudCertificacion;
    private double latitud;
    private double longitud;
    private String nombre;
    private int nroCertificacionAdquisicion;

    // Métodos Getter y Setter
    public long getCodigoEstacion(){
        return this.codigoEstacion;
    }
    public String getDocumentoCertificacionAdq(){
        return this.documentoCertificacionAdq;
    }
    public LocalDateTime fechaSolicitudCertificacion(){
        return this.fechaSolicitudCertificacion;
    }
    public double getLatitud(){
        return this.latitud;
    }
    public double getLongitud(){
        return this.longitud;
    }
    public String getNombre(){
        return this.nombre;
    }
    public int getNroCertificacionAdquisicion(){
        return this.nroCertificacionAdquisicion;
    }
    public void setCodigoEstacion(long codigoEstacion){
        this.codigoEstacion = codigoEstacion;
    }
    public void setDocumentoCertificacionAdq(String documentoCertificacionAdq){
        this.documentoCertificacionAdq = documentoCertificacionAdq;
    }
    public void setFechaSolicitudCertificacion(LocalDateTime fechaSolicitudCertificacion){
        this.fechaSolicitudCertificacion = fechaSolicitudCertificacion;
    }
    public void setLatitud(double latitud){
        this.latitud = latitud;
    }
    public void setLongitud(double longitud){
        this.longitud = longitud;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setNroCertificacionAdquisicion(int nroCertificacionAdquisicion){
        this.nroCertificacionAdquisicion = nroCertificacionAdquisicion;
    }
    
}
