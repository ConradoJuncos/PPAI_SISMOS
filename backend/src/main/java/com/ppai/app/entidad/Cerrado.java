package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class Cerrado extends Estado{

    // Hereda todos los métodos y atributos de la clase abstracta Estado

    // Método constructor
    public Cerrado(EventoSismico seleccionEventoSismico, LocalDateTime fechaHoraActual, Usuario usuarioLogueado){
        super("Cerrado", "EventoSismico");
        
    }
    
}
