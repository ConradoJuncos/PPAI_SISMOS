package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class DerivadoAExperto extends Estado {

    // Hereda todos los métodos y atributos de la clase abstracta Estado

    // Método constructor
    public DerivadoAExperto(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){

        super("DerivadoAExperto", "EventoSismico");
    }
    
}
