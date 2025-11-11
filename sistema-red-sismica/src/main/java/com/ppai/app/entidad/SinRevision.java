package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class SinRevision extends Estado {

    // Hereda todos los métodos y atributos de la clase abstracta Estado

    // Método constructor
    public SinRevision(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){

        super("SinRevision", "EventoSismico");
    }
    
}
