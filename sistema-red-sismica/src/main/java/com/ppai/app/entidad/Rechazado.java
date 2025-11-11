package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class Rechazado extends Estado {

    // Hereda todos los métodos y atributos de la clase abstracta Estado

    // Método constructor
    public Rechazado(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){

        super("Rechazado", "EventoSismico");
    }
    
}
