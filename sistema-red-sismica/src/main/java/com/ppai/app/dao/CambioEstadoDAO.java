package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.CambioEstado;
import com.ppai.app.entidad.Empleado;
import com.ppai.app.entidad.Estado;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CambioEstadoDAO {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    // Inserta un cambio de estado (estado histórico). La PK es (fechaHoraInicio, idEventoSismico)
    public void insert(CambioEstado ce) throws SQLException {
        String sql = "INSERT INTO CambioEstado (fechaHoraInicio, idEventoSismico, fechaHoraFin, idEmpleado, nombreEstado) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, format(ce.getFechaHoraInicio()));
            ps.setLong(2, ce.getIdEventoSismico());
            ps.setString(3, format(ce.getFechaHoraFin()));
            if (ce.getResponsableInspeccion() != null) {
                ps.setLong(4, ce.getResponsableInspeccion().getIdEmpleado());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, ce.getEstado() != null ? ce.getEstado().getNombreEstado() : null);
            ps.executeUpdate();
        }
    }

    // Cierra el cambio de estado actual (fechaHoraFin = NOW) para un evento
    public void cerrarCambioActual(long idEventoSismico, LocalDateTime fechaFin) throws SQLException {
        String sql = "UPDATE CambioEstado SET fechaHoraFin = ? WHERE idEventoSismico = ? AND fechaHoraFin IS NULL";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, format(fechaFin));
            ps.setLong(2, idEventoSismico);
            ps.executeUpdate();
        }
    }

    // Busca todos los cambios de estado de un evento, ordenados por inicio
    public List<CambioEstado> findByEvento(long idEventoSismico) throws SQLException {
        String sql = "SELECT * FROM CambioEstado WHERE idEventoSismico = ? ORDER BY fechaHoraInicio ASC";
        List<CambioEstado> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idEventoSismico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // Elimina un cambio por clave natural
    public void deleteByKey(LocalDateTime fechaHoraInicio, long idEventoSismico) throws SQLException {
        String sql = "DELETE FROM CambioEstado WHERE fechaHoraInicio = ? AND idEventoSismico = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, format(fechaHoraInicio));
            ps.setLong(2, idEventoSismico);
            ps.executeUpdate();
        }
    }

    private CambioEstado map(ResultSet rs) throws SQLException {
        CambioEstado ce = new CambioEstado();
        ce.setFechaHoraInicio(parse(rs.getString("fechaHoraInicio")));
        ce.setFechaHoraFin(parse(rs.getString("fechaHoraFin")));
        ce.setIdEventoSismico(rs.getLong("idEventoSismico"));
        long idEmp = rs.getLong("idEmpleado");
        if (!rs.wasNull()) {
            Empleado e = empleadoDAO.findById(idEmp);
            ce.setResponsableInspeccion(e);
        }
        String nombreEstado = rs.getString("nombreEstado");
        Estado estado = EstadoFactory.crear(nombreEstado);
        ce.setEstado(estado);
        // Nota: la entidad tiene idCambioEstado, pero la tabla usa PK compuesta. Dejamos id en 0.
        ce.setIdCambioEstado(0);
        return ce;
    }

    private static String format(LocalDateTime dt) {
        return dt == null ? null : dt.toString().replace('T', ' ');
        // almacenamos como "yyyy-MM-dd HH:mm:ss" al usar SQLite TEXT
    }

    private static LocalDateTime parse(String s) {
        if (s == null) return null;
        return LocalDateTime.parse(s.replace(' ', 'T'));
    }
}

