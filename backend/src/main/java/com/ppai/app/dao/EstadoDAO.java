package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*; // Importamos todas las entidades, incluyendo las subclases de Estado
import java.sql.*;
import java.util.*;

public class EstadoDAO {

    private static final String TABLE_NAME = "Estado";

    // ... insert, update, y otros métodos que gestionen la tabla 'Estado' ...

    /* --------------------------------------------------------------
       FIND BY CLAVE NATURAL – carga el estado y devuelve la subclase concreta
       -------------------------------------------------------------- */
    public Estado findByAmbitoAndNombre(String ambito, String nombreEstado) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ambito = ? AND nombreEstado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ambito);
            ps.setString(2, nombreEstado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return null; //mapResultSetToEstado(rs);
                }
            }
        }
        return null;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES: Lógica clave para el Patrón State
    // ==============================================================
    // private Estado mapResultSetToEstado(ResultSet rs) throws SQLException {
    //    String ambito = rs.getString("ambito");
    //    String nombre = rs.getString("nombreEstado");

        // Estado estado = new Estado();

        // Lógica clave: Instanciar la subclase concreta (Patrón State)
        /* switch (nombre) {
            case "Autodetectado":
                estado = new AutoDetectado(); 
                break;
            case "BloqueadoEnRevision":
                estado = new BloqueadoEnRevision();
                break;
            case "Confirmado":
                estado = new Confirmado();
                break;
            case "Rechazado":
                estado = new Rechazado();
                break;
            case "Anulado":
                estado = new Anulado();
                break;
            case "PendienteDeCierre":
                estado = new PendienteDeCierre();
                break;
            case "DerivadoAExperto":
                estado = new DerivadoAExperto();
                break;
            case "Cerrado":
                estado = new Cerrado();
                break;
            case "SinRevision":
                estado = new SinRevision();
                break;
            case "PendienteDeRevision":
                estado = new PendienteDeRevision();
                break;
            case "AutoConfirmado":
                estado = new AutoConfirmado();
                break;
            default:
                throw new SQLException("Estado desconocido o falta implementación para la subclase: " + nombre);
        } */

        // Inicializar atributos de la clase abstracta
        // estado.setAmbito(ambito);
        // estado.setNombreEstado(nombre);
        // return estado;
    //}

}