package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BloqueadoEnRevision extends Estado {

    // Hereda todos los métodos y atributos de la clase abstracta Estado

    // Método constructor con parametros
    public BloqueadoEnRevision() {

        // Lamando al constructor de la clase padre Estado
        super("BloqueadoEnRevision", "EventoSismico");
    }

    @Override
    public void rechazar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambioEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado) {
        Rechazado estadoCreadoRechazado = new Rechazado();
        Empleado empleado = obtenerResponsableDeInspeccion(usuarioLogueado);
        registrarCambioDeEstado(cambioEstado, fechaHoraActual, empleado, estadoCreadoRechazado);
        eventoSismicoSeleccionado.setEstadoActual(estadoCreadoRechazado);
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

    @Override
    public Empleado obtenerResponsableDeInspeccion(Usuario usuarioLogueado) {
        return usuarioLogueado.getEmpleado();
    }

}
