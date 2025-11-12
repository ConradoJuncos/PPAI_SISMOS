package com.ppai.app.entidad;

import java.time.LocalDateTime;

public class BloqueadoEnRevision extends Estado {

    // Hereda todos los métodos y atributos de la clase abstracta Estado

    // Método constructor con parametros
    public BloqueadoEnRevision() {

        // Lamando al constructor de la clase padre Estado
        super("BloqueadoEnRevision", "EventoSismico");
    }

//    protected void registrarCambioDeEstado() {
//
//        // Ejecutando la version del metodo de la clase padre (estado)
//        super.registrarCambioDeEstado();
//
//        // Buscar al cambio de estado actual a traves del evento sismico seleccionado
//        CambioEstado cambioEstadoActual = seleccionEventoSismico.obtenerCambioEstadoActual();
//
//        // Colocando la fecha actual como fecha fin del cambio de estado actual
//        cambioEstadoActual.setFechaHoraFin(fechaHoraActual);
//
//        // Obteniendo el empleado responsable de revision a través del usuario logueado
//        Empleado responsableInspeccion = obtenerResponsableDeInspeccion(usuarioLogueado);
//
//        // Creando el nuevo cambio de estado
//        CambioEstado nuevoCambioEstado = new CambioEstado(fechaHoraActual, this, responsableInspeccion);
//
//        // Seteando el nuevo estado concreto al evento sismico seleccionado
//        seleccionEventoSismico.setEstadoActual(this);
//    }

}
