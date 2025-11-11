package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class AutoDetectado extends Estado {
    
    // Hereda todos los métodos y atributos de la clase abstracta Estado

    // Método constructor
    public AutoDetectado(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){
        super("AutoDetectado", "EventoSismico");
    }

    // Sobrescribiendo el método de proximo estado
    @Override
    public void crearProximoEstado(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){

        // Creando el proximo estado del evento sismico AutoDetectado --> BloqueadoEnRevision
        BloqueadoEnRevision bloqueadoEnRevision = new BloqueadoEnRevision(seleccionEventoSismico, fechaHoraActual, usuarioLogueado);
    }

}
