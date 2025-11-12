package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EstadoDAO {

    private static final String TABLE_NAME = "Estado";

    public Estado findByNombreYAmbito(String nombreEstado, String ambitoEstado) throws SQLException {
        return findByAmbitoAndNombre(ambitoEstado, nombreEstado);
    }

    // --- Buscar un estado concreto por nombreEstado y ámbito ---
    public Estado findByPk(String nombreEstado, String ambitoEstado) throws SQLException {
        String sql = "SELECT * FROM Estado WHERE nombreEstado = ? AND ambitoEstado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreEstado);
            ps.setString(2, ambitoEstado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Aquí creamos la subclase concreta según el nombreEstado
                    return materializarEstado(rs.getString("nombreEstado"), rs.getString("ambitoEstado"));
                }
            }
        }
        return null;
    }

    // --- Crear el tipo de estado concreto ---
    private Estado materializarEstado(String nombreEstado, String ambitoEstado) throws SQLException {
        switch (nombreEstado) {
            case "AutoDetectado":
                return new AutoDetectado(null, null, null);
            case "PendienteDeRevision":
                return new PendienteDeRevision(null, null, null);
            case "BloqueadoEnRevision":
                return new BloqueadoEnRevision();
            case "DerivadoAExperto":
                return new DerivadoAExperto(null, null, null);
            case "Confirmado":
                return new Confirmado(null, null, null);
            case "AutoConfirmado":
                return new AutoConfirmado(null, null, null);
            case "Rechazado":
                return new Rechazado(null, null, null);
            case "Anulado":
                return new Anulado(null, null, null);
            case "PendienteDeCierre":
                return new PendienteDeCierre(null, null, null);
            case "Cerrado":
                return new Cerrado(null, null, null);
            case "SinRevision":
                return new SinRevision(null, null, null);
            case "Activo":
                return null; // no se trabaja con estados de series temporales para esta implementacion
            case "Inactivo":
                return null;
            default:
                throw new SQLException("Estado desconocido o sin implementación: " + nombreEstado);
        }
    }

    // --- Insertar nuevo estado (si aplica en tabla de configuración, no dinámica)
    // ---
    public void insert(Estado estado) throws SQLException {
        String sql = "INSERT INTO Estado (nombreEstado, ambitoEstado) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado.getNombreEstado());
            ps.setString(2, estado.getAmbito());
            ps.executeUpdate();
        }
    }

    // --- Listar todos los estados registrados ---
    public List<Estado> findAll() throws SQLException {
        List<Estado> estados = new ArrayList<>();

        String sql = "SELECT * FROM Estado";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nombreEstado");
                String ambito = rs.getString("ambitoEstado");

                // reutiliza tu método existente de materialización
                Estado e = materializarEstado(nombre, ambito);
                estados.add(e);
            }
        }

        return estados;
    }

    public Estado findByAmbitoAndNombre(String ambito, String nombreEstado) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ambitoEstado = ? AND nombreEstado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ambito);
            ps.setString(2, nombreEstado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Se materializa el estado concreto según el nombre
                    return materializarEstado(rs.getString("nombreEstado"), rs.getString("ambitoEstado"));
                }
            }
        }
        return null;
    }
}
