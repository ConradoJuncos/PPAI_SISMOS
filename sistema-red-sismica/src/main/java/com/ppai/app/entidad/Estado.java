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

    public void revisar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado) {
    }

    public void derivar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){}

    public void confirmar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambioEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){}

    public void rechazar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambioEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){}



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
