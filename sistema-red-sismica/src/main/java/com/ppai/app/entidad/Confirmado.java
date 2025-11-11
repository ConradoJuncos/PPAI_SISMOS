package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class Confirmado extends Estado{

    // Hereda todos los métodos y atributos de la clase abstracta Estado

    // Método constructor
    public Confirmado(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){

        super("Confirmado", "EventoSismico");
    }
    
}
