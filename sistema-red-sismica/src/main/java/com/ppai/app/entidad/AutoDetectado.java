package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AutoDetectado extends Estado {
    public AutoDetectado(){
        super("AutoDetectado");
    }

    @Override
    public void revisar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado) {
        BloqueadoEnRevision estadoCreadoBloqueadoEnRevision = (BloqueadoEnRevision) crearProximoEstado("BloqueadoEnRevision");
        Empleado empleado = obtenerResponsableDeInspeccion(usuarioLogueado);
        registrarCambioDeEstado(cambiosEstado, fechaHoraActual, empleado, estadoCreadoBloqueadoEnRevision);
        eventoSismicoSeleccionado.setEstadoActual(estadoCreadoBloqueadoEnRevision);
    }

    // Sobrescribiendo el método de proximo estado
    @Override
    public Estado crearProximoEstado(String nombreEstado){
        // Creando el proximo estado del evento sismico AutoDetectado --> BloqueadoEnRevision
        return(new BloqueadoEnRevision());
    }

    public void registrarCambioDeEstado(ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Empleado empleado, Estado nuevoEstado) {
        for (CambioEstado cambioEstado : cambiosEstado) {
            if (cambioEstado.esEstadoActual()) {
                cambioEstado.setFechaHoraFin(fechaHoraActual);
            }
        }
        CambioEstado nuevoCambioEstado = new CambioEstado(fechaHoraActual, nuevoEstado, empleado);
        cambiosEstado.add(nuevoCambioEstado);
    }

}
