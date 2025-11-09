package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;

public class EstadoDAO {

    private static final String TABLE_NAME = "Estado";

    public Estado findByNombreYAmbito(String nombreEstado, String ambito) throws SQLException {
        return findByAmbitoAndNombre(ambito, nombreEstado);
    }

    // --- Buscar un estado concreto por nombreEstado y ámbito ---
    public Estado findByPk(String nombreEstado, String ambito) throws SQLException {
        String sql = "SELECT * FROM Estado WHERE nombreEstado = ? AND ambito = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreEstado);
            ps.setString(2, ambito);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Aquí creamos la subclase concreta según el nombreEstado
                    return materializarEstado(rs.getString("nombreEstado"), rs.getString("ambito"));
                }
            }
        }
        return null;
    }

    // --- Crear el tipo de estado concreto ---
    private Estado materializarEstado(String nombreEstado, String ambito) throws SQLException {
        switch (nombreEstado) {
            case "AutoDetectado":
                return new AutoDetectado(null, null, null);
            case "PendienteDeRevision":
                return new PendienteDeRevision(null, null, null);
            case "BloqueadoEnRevision":
                return new BloqueadoEnRevision(null, null, null);
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
            default:
                throw new SQLException("Estado desconocido o sin implementación: " + nombreEstado);
        }
    }

    // --- Insertar nuevo estado (si aplica en tabla de configuración, no dinámica)
    // ---
    public void insert(Estado estado) throws SQLException {
        String sql = "INSERT INTO Estado (nombreEstado, ambito) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado.getNombreEstado());
            ps.setString(2, estado.getAmbito());
            ps.executeUpdate();
        }
    }

    // --- Listar todos los estados registrados ---
    public void findAll() throws SQLException {
        String sql = "SELECT * FROM Estado";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.printf("Estado: %s | Ámbito: %s%n", rs.getString("nombreEstado"), rs.getString("ambito"));
            }
        }
    }

    public Estado findByAmbitoAndNombre(String ambito, String nombreEstado) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ambito = ? AND nombreEstado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ambito);
            ps.setString(2, nombreEstado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Se materializa el estado concreto según el nombre
                    return materializarEstado(rs.getString("nombreEstado"), rs.getString("ambito"));
                }
            }
        }
        return null;
    }
}
