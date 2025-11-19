package com.ppai.app.dao;

import com.ppai.app.entidad.*;

/**
 * Factory central para materializar instancias concretas de Estado
 * a partir del nombre almacenado en la base de datos.
 * Las tablas de estados concretos (AutoDetectado, Rechazado, etc.)
 * en el esquema actual sólo guardan id y nombre; aquí se crea
 * la subclase correspondiente.
 */
public class EstadoFactory {

    public static Estado crear(String nombreEstado) {
        if (nombreEstado == null) return null;
        return switch (nombreEstado) {
            case "AutoDetectado" -> new AutoDetectado();
            case "PendienteDeRevision" -> new PendienteDeRevision();
            case "BloqueadoEnRevision" -> new BloqueadoEnRevision();
            case "Derivado" -> new Derivado();
            case "ConfirmadoPorPersonal" -> new ConfirmadoPorPersonal();
            case "Rechazado" -> new Rechazado();
            case "PendienteDeCierre" -> new PendienteDeCierre();
            case "Cerrado" -> new Cerrado();
            case "AutoConfirmado" -> new AutoConfirmado();
            case "SinRevision" -> new SinRevision();
            default -> null; // Estado desconocido
        };
    }
}
