package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla CambioEstadoSismografo que registra el historial
 * de cambios de estado de un sismógrafo.
 * PK compuesta: (fechaHoraInicio, identificadorSismografo)
 */
public class CambioEstadoSismografoDAO {

    public static class CambioEstadoSismografo {
        private LocalDateTime fechaHoraInicio;
        private LocalDateTime fechaHoraFin;
        private long identificadorSismografo;
        private long idEstadoSismografo;
        private String nombreEstado; // cache del nombre para facilitar consultas

        public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
        public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }
        public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
        public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }
        public long getIdentificadorSismografo() { return identificadorSismografo; }
        public void setIdentificadorSismografo(long identificadorSismografo) { this.identificadorSismografo = identificadorSismografo; }
        public long getIdEstadoSismografo() { return idEstadoSismografo; }
        public void setIdEstadoSismografo(long idEstadoSismografo) { this.idEstadoSismografo = idEstadoSismografo; }
        public String getNombreEstado() { return nombreEstado; }
        public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }

        public boolean esEstadoActual() {
            return fechaHoraFin == null;
        }
    }

    private final EstadoSismografoDAO estadoDAO = new EstadoSismografoDAO();

    public void insert(CambioEstadoSismografo cambio) throws SQLException {
        String sql = "INSERT INTO CambioEstadoSismografo (fechaHoraInicio, identificadorSismografo, fechaHoraFin, idEstadoSismografo) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, format(cambio.getFechaHoraInicio()));
            ps.setLong(2, cambio.getIdentificadorSismografo());
            ps.setString(3, format(cambio.getFechaHoraFin()));
            ps.setLong(4, cambio.getIdEstadoSismografo());
            ps.executeUpdate();
        }
    }

    public void cerrarCambioActual(long idSismografo, LocalDateTime fechaFin) throws SQLException {
        String sql = "UPDATE CambioEstadoSismografo SET fechaHoraFin = ? WHERE identificadorSismografo = ? AND fechaHoraFin IS NULL";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, format(fechaFin));
            ps.setLong(2, idSismografo);
            ps.executeUpdate();
        }
    }

    public List<CambioEstadoSismografo> findBySismografo(long idSismografo) throws SQLException {
        String sql = "SELECT * FROM CambioEstadoSismografo WHERE identificadorSismografo = ? ORDER BY fechaHoraInicio ASC";
        List<CambioEstadoSismografo> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public CambioEstadoSismografo findActual(long idSismografo) throws SQLException {
        String sql = "SELECT * FROM CambioEstadoSismografo WHERE identificadorSismografo = ? AND fechaHoraFin IS NULL";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public void deleteByKey(LocalDateTime fechaHoraInicio, long idSismografo) throws SQLException {
        String sql = "DELETE FROM CambioEstadoSismografo WHERE fechaHoraInicio = ? AND identificadorSismografo = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, format(fechaHoraInicio));
            ps.setLong(2, idSismografo);
            ps.executeUpdate();
        }
    }

    private CambioEstadoSismografo map(ResultSet rs) throws SQLException {
        CambioEstadoSismografo c = new CambioEstadoSismografo();
        c.setFechaHoraInicio(parse(rs.getString("fechaHoraInicio")));
        c.setFechaHoraFin(parse(rs.getString("fechaHoraFin")));
        c.setIdentificadorSismografo(rs.getLong("identificadorSismografo"));
        c.setIdEstadoSismografo(rs.getLong("idEstadoSismografo"));

        // Cargar nombre del estado para facilitar consultas
        long idEstado = c.getIdEstadoSismografo();
        EstadoSismografoDAO.EstadoSismografo estado = estadoDAO.findById(idEstado);
        if (estado != null) {
            c.setNombreEstado(estado.getNombre());
        }

        return c;
    }

    private static String format(LocalDateTime dt) {
        return dt == null ? null : dt.toString().replace('T', ' ');
    }

    private static LocalDateTime parse(String s) {
        if (s == null) return null;
        return LocalDateTime.parse(s.replace(' ', 'T'));
    }
}

