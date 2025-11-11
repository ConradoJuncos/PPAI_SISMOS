package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Suscripcion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Suscripcion.
 * Compatible con UsuarioDAO y el nuevo esquema de la base de datos.
 * Incluye métodos CRUD y relación N:N con Usuario.
 */
public class SuscripcionDAO {

    /* ==============================================================
       INSERT
       ============================================================== */
    public void insert(Suscripcion s) throws SQLException {
        String sql = """
            INSERT INTO Suscripcion (fechaHoraInicioSuscripcion, fechaHoraFinSuscripcion)
            VALUES (?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, s.getFechaHoraInicioSuscripcion());
            ps.setObject(2, s.getFechaHoraFinSuscripcion());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    s.setIdSuscripcion(rs.getLong(1));
                }
            }
        }
    }

    /* ==============================================================
       UPDATE
       ============================================================== */
    public void update(Suscripcion s) throws SQLException {
        String sql = """
            UPDATE Suscripcion
            SET fechaHoraInicioSuscripcion = ?, fechaHoraFinSuscripcion = ?
            WHERE idSuscripcion = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, s.getFechaHoraInicioSuscripcion());
            ps.setObject(2, s.getFechaHoraFinSuscripcion());
            ps.setLong(3, s.getIdSuscripcion());
            ps.executeUpdate();
        }
    }

    /* ==============================================================
       DELETE
       ============================================================== */
    public void delete(long idSuscripcion) throws SQLException {
        String sql = "DELETE FROM Suscripcion WHERE idSuscripcion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSuscripcion);
            ps.executeUpdate();
        }
    }

    /* ==============================================================
       FIND BY ID
       ============================================================== */
    public Suscripcion findById(long idSuscripcion) throws SQLException {
        String sql = "SELECT * FROM Suscripcion WHERE idSuscripcion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idSuscripcion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSuscripcion(rs);
                }
            }
        }
        return null;
    }

    /* ==============================================================
       FIND ALL
       ============================================================== */
    public List<Suscripcion> findAll() throws SQLException {
        List<Suscripcion> list = new ArrayList<>();
        String sql = "SELECT * FROM Suscripcion";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToSuscripcion(rs));
            }
        }
        return list;
    }

    /* ==============================================================
       RELACIÓN N:N — Usuario–Suscripción
       ============================================================== */
    public List<Suscripcion> findByUsuarioId(long idUsuario) throws SQLException {
        String sql = """
            SELECT s.*
            FROM Suscripcion s
            JOIN Usuario_Suscripcion us ON s.idSuscripcion = us.idSuscripcion
            WHERE us.idUsuario = ?
        """;

        List<Suscripcion> suscripciones = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suscripciones.add(mapResultSetToSuscripcion(rs));
                }
            }
        }
        return suscripciones;
    }

    /* ==============================================================
       AUXILIARES
       ============================================================== */
    private Suscripcion mapResultSetToSuscripcion(ResultSet rs) throws SQLException {
        Suscripcion s = new Suscripcion();
        s.setIdSuscripcion(rs.getLong("idSuscripcion"));
        s.setFechaHoraInicioSuscripcion(getLocalDateTime(rs, "fechaHoraInicioSuscripcion"));
        s.setFechaHoraFinSuscripcion(getLocalDateTime(rs, "fechaHoraFinSuscripcion"));
        return s;
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
