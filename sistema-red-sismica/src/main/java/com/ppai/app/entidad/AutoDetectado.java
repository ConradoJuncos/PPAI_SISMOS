package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AutoDetectado extends Estado {
    public AutoDetectado(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){
        super("AutoDetectado", "EventoSismico");
    }

    @Override
    public void bloquearPorRevision(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado) {
        BloqueadoEnRevision estadoCreadoBloqueadoEnRevision = (BloqueadoEnRevision) crearProximoEstado();
        registrarCambioDeEstado(cambiosEstado, fechaHoraActual, usuarioLogueado, estadoCreadoBloqueadoEnRevision);
        eventoSismicoSeleccionado.setEstadoActual(estadoCreadoBloqueadoEnRevision);
    }

    // Sobrescribiendo el método de proximo estado
    @Override
    public Estado crearProximoEstado(){
        // Creando el proximo estado del evento sismico AutoDetectado --> BloqueadoEnRevision
        return(new BloqueadoEnRevision());
    }

    public void registrarCambioDeEstado(ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado, Estado nuevoEstado) {
        for (CambioEstado cambioEstado : cambiosEstado) {
            if (cambioEstado.esEstadoActual()) {
                cambioEstado.setFechaHoraFin(fechaHoraActual);
            }
        }
        CambioEstado nuevoCambioEstado = new CambioEstado(fechaHoraActual, nuevoEstado, usuarioLogueado.getEmpleado());
        cambiosEstado.add(nuevoCambioEstado);
    }

}
