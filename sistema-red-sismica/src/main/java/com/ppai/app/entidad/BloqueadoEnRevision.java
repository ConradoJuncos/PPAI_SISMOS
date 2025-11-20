package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BloqueadoEnRevision extends Estado {

    // Hereda todos los métodos y atributos de la clase abstracta Estado

    // Método constructor con parametros
    public BloqueadoEnRevision() {

        // Lamando al constructor de la clase padre Estado
        super("BloqueadoEnRevision");
    }

    @Override
    public void rechazar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado) {
        Rechazado estadoCreadoRechazado = (Rechazado) crearProximoEstado("Rechazado");
        Empleado empleado = obtenerResponsableDeInspeccion(usuarioLogueado);
        registrarCambioDeEstado(cambiosEstado, fechaHoraActual, empleado, estadoCreadoRechazado);
        eventoSismicoSeleccionado.setEstadoActual(estadoCreadoRechazado);
    }

    @Override
    public void confirmar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado) {
        ConfirmadoPorPersonal estadoCreadoConfirmadoPorPersonal = (ConfirmadoPorPersonal) crearProximoEstado("ConfirmadoPorPersonal");
        Empleado empleado = obtenerResponsableDeInspeccion(usuarioLogueado);
        registrarCambioDeEstado(cambiosEstado, fechaHoraActual, empleado, estadoCreadoConfirmadoPorPersonal);
        eventoSismicoSeleccionado.setEstadoActual(estadoCreadoConfirmadoPorPersonal);
    }

    @Override
    public void derivar(EventoSismico eventoSismicoSeleccionado, ArrayList<CambioEstado> cambiosEstado, LocalDateTime fechaHoraActual, Usuario usuarioLogueado) {
        Derivado estadoCreadoDerivado = (Derivado) crearProximoEstado("Derivado");
        Empleado empleado = obtenerResponsableDeInspeccion(usuarioLogueado);
        registrarCambioDeEstado(cambiosEstado, fechaHoraActual, empleado, estadoCreadoDerivado);
        eventoSismicoSeleccionado.setEstadoActual(estadoCreadoDerivado);
    }

    @Override
    public Estado crearProximoEstado(String nombreEstado) {
        return switch (nombreEstado) {
            case "Rechazado" -> new Rechazado();
            case "ConfirmadoPorPersonal" -> new ConfirmadoPorPersonal();
            case "Derivado" -> new Derivado();
            default -> throw new IllegalArgumentException("Estado no válido: " + nombreEstado);
        };
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

}
