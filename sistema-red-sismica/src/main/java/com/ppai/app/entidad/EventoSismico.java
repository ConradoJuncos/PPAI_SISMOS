package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventoSismico {
   
    // Atributos
    private long idEventoSismico;
    private LocalDateTime fechaHoraFin;
    private LocalDateTime fechaHoraOcurrencia;
    private String latitudEpicentro;
    private String latitudHipocentro;
    private String longitudEpicentro;
    private String longitudHipocentro;
    private double valorMagnitud; 
    private ClasificacionSismo clasificacionSismo;
    private MagnitudRichter magnitudRichter;
    private OrigenDeGeneracion origenGeneracion;
    private AlcanceSismo alcanceSismo;
    private List<SerieTemporal> serieTemporal = new  ArrayList<SerieTemporal>();
    private Estado estadoActual; 
    private List<CambioEstado> cambioEstado = new ArrayList<CambioEstado>();
    private Empleado analistaSupervisor; 

    // Método constructor sin parámetros
    public EventoSismico(){}

    // Constructor (No incluye id por ser autogenerado, y no incluye analista supervisor por ser no revisado)
    public EventoSismico(LocalDateTime fechaHoraOcurrencia, String latitudEpicentro, String latitudHipocentro,
        String longitudEpicentro, String longitudHipocentro, double valorMagnitud, ClasificacionSismo clasificacionSismo,
        MagnitudRichter magnitudRichter, OrigenDeGeneracion origenGeneracion, AlcanceSismo alcanceSismo, List<SerieTemporal> serieTemporal,
        Estado estadoActual, List<CambioEstado> cambioEstado){
            this.fechaHoraOcurrencia = fechaHoraOcurrencia;
            this.latitudEpicentro = latitudEpicentro;
            this.latitudHipocentro = latitudHipocentro;
            this.valorMagnitud = valorMagnitud;
            this.clasificacionSismo = clasificacionSismo;
            this.magnitudRichter = magnitudRichter;
            this.origenGeneracion = origenGeneracion;
            this.alcanceSismo = alcanceSismo;
            this.serieTemporal = serieTemporal;
            this.cambioEstado = cambioEstado;
    }

    // Comportamiento
    // Verificar si el evento sismico 
    public boolean esAutoDetectado(){ 

        // Consultar al estadoActual si es Auto Detectado
        if (this.estadoActual.sosAutoDetectado()) {

            return true;
        }

        return false;
    }

    // Preguntar si el evento sismico seleccionado es no revisado (no tiene analista asignado)
    public boolean sosNoRevisado(){

        // Verificar si hay un analista supervisor asignado
        if (this.analistaSupervisor == null) {

            // No nay analista supervisor asignado, es no revisado
            return true;
        }

        // Hay analsta supervisor asignado, es revisado
        return false;
    }

    // Obtener los datos princpales
    public String obtenerDatosPrincipales(){

        String datosPrincipales = "";

        datosPrincipales += getFechaHoraOcurrencia().toString() + ", ";
        datosPrincipales += getLatitudEpicentro().toString() + ", ";
        datosPrincipales += getLatitudHipocentro().toString() + ", ";
        datosPrincipales += getLongitudEpicentro().toString() + ", ";
        datosPrincipales += getLongitudHipocentro().toString();

        return datosPrincipales;

    }

    // Verificar si un conjunto de datos principales son propios de un evento sismico
    public EventoSismico sonMisDatosPrincipales(String datosPrincipales){
     
        // Comparando el string con los datos principales del evento sismico
        if (datosPrincipales != null && datosPrincipales.equals(obtenerDatosPrincipales())){

            // Se retorna a si mismo
            return this;
        }

        // Caso contrario, los datos principales no son propios de este vento sismico
        return null;
    }

    // Bloquear por revisión el evento sismico
    public void bloquearPorRevision(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){

        // Delegación del cambio de estado al estado concreto actual
        this.estadoActual.bloquearPorRevision(seleccionEventoSismico, fechaHoraActual, usuarioLogueado);
    }

    // Obtener el Cambio de Estado (Historial de Estado) Actual del Evento
    public CambioEstado obtenerCambioEstadoActual(){

        // Sobre los cambio de estado del evento sismico
        for (CambioEstado hitorialEstado : cambioEstado){
            
            // Si el cambio de estao es actual
            if (hitorialEstado.esEstadoActual()){

                // se retorna dicho cambio de estado
                return hitorialEstado;
            }
        }

        return null; // Para evitar fallos
    }

    // Métodos Getter y Setter
    public long getIdEventoSismico(){
        return this.idEventoSismico;
    }
    public LocalDateTime getFechaHoraFin(){
        return this.fechaHoraFin;
    }
    public LocalDateTime getFechaHoraOcurrencia(){
        return this.fechaHoraOcurrencia;
    }
    public String getLatitudEpicentro(){
        return this.latitudEpicentro;
    }
    public String getLatitudHipocentro(){
        return this.latitudHipocentro;
    }
    public String getLongitudEpicentro(){
        return this.longitudEpicentro;
    }
    public String getLongitudHipocentro(){
        return this.longitudHipocentro;
    }
    public double getValorMagnitud(){
        return this.valorMagnitud;
    }
    public ClasificacionSismo getClasificacionSismo(){
        return this.clasificacionSismo;
    }
    public MagnitudRichter getMagnitudRichter(){
        return this.magnitudRichter;
    }
    public OrigenDeGeneracion getOrigenGegeneracion(){
        return this.origenGeneracion;
    }
    public AlcanceSismo getAlcanceSismo(){
        return this.alcanceSismo;
    }
    public List<SerieTemporal> getSerieTemporal(){
        return this.serieTemporal;
    }
    public Estado getEstadoActual(){
        return this.estadoActual;
    }
    public List<CambioEstado> getCambioEstado(){
        return this.cambioEstado;
    }
    public Empleado getAnalistaSupervisor(){
        return this.analistaSupervisor;
    }
    public void setIdEventoSismico(long idEventoSismico){
        this.idEventoSismico = idEventoSismico;
    }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin){
        this.fechaHoraFin = fechaHoraFin;
    }
    public void setFechaHoraOcurrencia(LocalDateTime fechaHoraOcurrencia){
        this.fechaHoraOcurrencia = fechaHoraOcurrencia;
    }
    public void setLatitudEpicentro(String latitudEpicentro){
        this.latitudEpicentro = latitudEpicentro;
    }
    public void setLatitudHipocentro(String latitudHipocentro){
        this.latitudHipocentro = latitudHipocentro;
    }
    public void setLongitudEpicentro(String longitudEpicentro){
        this.longitudEpicentro = longitudEpicentro;
    }
    public void setLongitudHipocentro(String longitudHipocentro){
        this.longitudHipocentro = longitudHipocentro;
    }
    public void setValorMagnitud (double valorMagnitud){
        this.valorMagnitud = valorMagnitud;
    }
    public void setClasificacionSismo(ClasificacionSismo clasificacionSismo){
        this.clasificacionSismo = clasificacionSismo;
    }
    public void setMagnitudRichter(MagnitudRichter magnitudRichter){
        this.magnitudRichter = magnitudRichter;
    }
    public void setOrigenDeGeneracion(OrigenDeGeneracion origenGeneracion){
        this.origenGeneracion = origenGeneracion;
    }
    public void setAlcanceSismo(AlcanceSismo alcanceSismo){
        this.alcanceSismo = alcanceSismo;
    }
    public void setSerieTemporal(List<SerieTemporal> serieTemporal){
        this.serieTemporal = serieTemporal;
    }
    public void setEstadoActual(Estado estadoActual){
        this.estadoActual = estadoActual;
    }
    public void setCambioEstado(List<CambioEstado> cambioEstado){
        this.cambioEstado = cambioEstado;
    }
    public void setAnalistaSupervisor(Empleado analistaSupervisor){
        this.analistaSupervisor = analistaSupervisor;
    }
}
