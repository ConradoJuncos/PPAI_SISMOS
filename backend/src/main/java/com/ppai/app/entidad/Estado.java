package com.ppai.app.entidad;

// Clase Abstracta de Estado (Patrón State)
public abstract class Estado {
    
    // Atributos
    private long idEstado;
    private String ambito;
    private String nombreEstado;

    // Comportamiento
    public void crearProximoEstado(){
        // implementa proximamente    
    }

    public void registrarCambioDeEstado() {
        // Implementar proximamente
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

    // reviar parámetros y retorno
    public void bloquearEvento(){}

    // revisar parámetros y retorno
    public void derivarAExperto(){}

    // revisar parámetros y retorno
    public void resolverDerivacion(){}

    // revisar parámetros y retornos
    public void confirmarEvento(){}

    // revisar parámetros y retorno
    public void rechazarEvento(){}

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
    public long getIdEstado(){
        return this.idEstado;
    }
    public String getAmbito(){
        return this.ambito;
    }
    public String getNombreEstado(){
        return this.nombreEstado;
    }
    public void setIdEstado(long idEstado){
        this.idEstado = idEstado;
    }
    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }
    public void setNombreEstado(String nombreEstado){
        this.nombreEstado = nombreEstado;
    }
}
