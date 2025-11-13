package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;

// Clase Abstracta de Estado (Patrón State)
public abstract class Estado {
    
    // Atributos
    private String nombreEstado;
    private String ambito;

    // Método constructor con parámetros
    public Estado(String nombreEstado, String ambito){
        this.nombreEstado = nombreEstado;
        this.ambito = ambito;
    }

    // Comportamiento
    public Estado crearProximoEstado(){
        return null;
    }

    protected void registrarCambioDeEstado(ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado, Estado nuevoEstado) {
    }

    protected Empleado obtenerResponsableDeInspeccion(Usuario usuarioLogueado) {

        // Se obtiene el empleado (respondable de revision del evento sismico / responsable del cambio de estado)
        Empleado responsableDeInspeccion = usuarioLogueado.obtenerEmpleado();

        // Retornando el empleado responsable de revision
        return responsableDeInspeccion;
    }

    // Consultar si el estado es auto detectado
    public boolean sosAutoDetectado() {

        if (this.nombreEstado == "AutoDetectado"){
            // Es auto detectado
            return true;
        }
        // No es auto detectado
        return false;
    }

    // revisar parámetros y retorno
    public void registrarEventoAutoDetectado(){

    }

    // revisar parámetros y retorno
    public void registrarEventoAutoConfirmado(){}

    // revisar parámetros y retorno
    public void noRevisadoEnTiempo(){}

    // revisar parámetros y retorno
    public void anularEvento(){}

    // revisar parámetros y retorno
    public void aceptarParaRevision(){}

    // ESTE ES EL IMPORTANTE!!!!!
    public void revisar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado) {
    }
    // revisar parámetros y retorno
    public void derivar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){}

    // revisar parámetros y retorno
    public void resolverDerivacion(){}

    // revisar parámetros y retornos
    public void confirmar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambioEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){}

    // revisar parámetros y retorno
    public void rechazar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambioEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){}

    // revisar parámetros y retorno
    public void expirarVentanaTemporal(){}

    // revisar parámetros y retorno
    public void cerrarEvento(){}

    // revisar parámetros y retorno
    public void cancelarRevision(){}

    // revisar parámetros y retorno
    public void notificarAnalista(){}

    // revisar parámetros y retorno
    public void notificarSuscriptores(){}

    // revisar parámetros y retorno
    public void generarAlertaAutomatica(){}

    // revisar parámetros y retorno
    public void iniciarRevisionManual(){}

    // revisar parámetros y retorno
    public void finalizarRevisionManual(){}

    // revisar parámetros y retorno
    public void actualizarEstadoEvento(){}    

    // revisar parámetros y retorno
    public void registrarMagnitud(){}

    // revisar parámetros y retorno
    public void procesarDeteccionAutomatica(){}    

    // revisar parámetros y retorno
    public void actualizarVentanaTemporal(){}

    // revisar parámetros y retorno
    public void emitirInformeCierre(){}

    // Métodos Getter y Setter
    public String getAmbito(){
        return this.ambito;
    }
    public String getNombreEstado(){
        return this.nombreEstado;
    }
    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }
    public void setNombreEstado(String nombreEstado){
        this.nombreEstado = nombreEstado;
    }
}
