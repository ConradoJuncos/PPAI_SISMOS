package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AutoDetectado extends Estado {
    public AutoDetectado(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){
        super("AutoDetectado", "EventoSismico");
    }

    @Override
    public void bloquearPorRevision(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambioEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado) {
        System.out.println("f");
    }

    // Sobrescribiendo el método de proximo estado
    @Override
    public void crearProximoEstado(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){

        // Creando el proximo estado del evento sismico AutoDetectado --> BloqueadoEnRevision
        BloqueadoEnRevision bloqueadoEnRevision = new BloqueadoEnRevision(seleccionEventoSismico, fechaHoraActual, usuarioLogueado);
    }

}
