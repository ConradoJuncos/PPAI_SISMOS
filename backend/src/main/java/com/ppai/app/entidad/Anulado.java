package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class Anulado extends Estado {

    // Hereda todos los métodos y atributos de la clase abstracta Estado

    public Anulado(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){
        super("Anulado", "EventoSismico");
    }
}
