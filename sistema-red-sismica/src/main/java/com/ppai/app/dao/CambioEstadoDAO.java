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

    /**
     * Inserta un cambio de estado completo:
     * 1. Inserta en CambioEstado (genera idCambioEstado)
     * 2. Inserta el estado concreto en su tabla (AutoDetectado, BloqueadoEnRevision, etc.)
     * 3. Crea la relación en la tabla intermedia (CambioEstado_AutoDetectado, etc.)
     */
    public void insert(CambioEstado ce) throws SQLException {
        Connection c = DatabaseConnection.getConnection();
        try {
            c.setAutoCommit(false); // Transacción para garantizar atomicidad

            // 1. Insertar en CambioEstado
            String sql = "INSERT INTO CambioEstado (fechaHoraInicio, idEventoSismico, fechaHoraFin, idEmpleado, nombreEstado) VALUES (?, ?, ?, ?, ?)";
            long idCambioEstado;
            String nombreEstado = ce.getEstado() != null ? ce.getEstado().getNombreEstado() : null;

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, format(ce.getFechaHoraInicio()));
                ps.setLong(2, ce.getIdEventoSismico());
                ps.setString(3, format(ce.getFechaHoraFin()));
                if (ce.getResponsableInspeccion() != null) {
                    ps.setLong(4, ce.getResponsableInspeccion().getIdEmpleado());
                } else {
                    ps.setNull(4, Types.INTEGER);
                }
                ps.setString(5, nombreEstado);
                ps.executeUpdate();
            }

            // Obtener ID generado usando last_insert_rowid() de SQLite
            try (Statement stmt = c.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    idCambioEstado = rs.getLong(1);
                    ce.setIdCambioEstado(idCambioEstado);
                } else {
                    throw new SQLException("No se pudo obtener el ID del CambioEstado insertado");
                }
            }

            // 2. Insertar estado concreto y 3. Crear relación
            if (nombreEstado != null) {
                insertEstadoConcreto(c, idCambioEstado, nombreEstado);
            }

            c.commit();
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
            c.close();
        }
    }

    /**
     * Inserta el estado concreto en su tabla específica y crea la relación intermedia
     */
    private void insertEstadoConcreto(Connection c, long idCambioEstado, String nombreEstado) throws SQLException {
        long idEstadoConcreto;

        switch (nombreEstado) {
            case "AutoDetectado":
                idEstadoConcreto = insertAutoDetectado(c);
                insertRelacion(c, "CambioEstado_AutoDetectado", idCambioEstado, "idAutoDetectado", idEstadoConcreto);
                break;
            case "BloqueadoEnRevision":
                idEstadoConcreto = insertBloqueadoEnRevision(c);
                insertRelacion(c, "CambioEstado_BloqueadoEnRevision", idCambioEstado, "idBloqueadoEnRevision", idEstadoConcreto);
                break;
            case "Rechazado":
                idEstadoConcreto = insertRechazado(c);
                insertRelacion(c, "CambioEstado_Rechazado", idCambioEstado, "idRechazado", idEstadoConcreto);
                break;
            case "Derivado":
                idEstadoConcreto = insertDerivado(c);
                insertRelacion(c, "CambioEstado_Derivado", idCambioEstado, "idDerivado", idEstadoConcreto);
                break;
            case "ConfirmadoPorPersonal":
                idEstadoConcreto = insertConfirmadoPorPersonal(c);
                insertRelacion(c, "CambioEstado_ConfirmadoPorPersonal", idCambioEstado, "idConfirmadoPorPersonal", idEstadoConcreto);
                break;
            case "PendienteDeRevision":
                idEstadoConcreto = insertPendienteDeRevision(c);
                insertRelacion(c, "CambioEstado_PendienteDeRevision", idCambioEstado, "idPendienteDeRevision", idEstadoConcreto);
                break;
            case "SinRevision":
                // No hay tabla intermedia para SinRevision
                insertSinRevision(c);
                break;
            case "Cerrado":
                idEstadoConcreto = insertCerrado(c);
                insertRelacion(c, "CambioEstado_Cerrado", idCambioEstado, "idCerrado", idEstadoConcreto);
                break;
            case "PendienteDeCierre":
                idEstadoConcreto = insertPendienteDeCierre(c);
                insertRelacion(c, "CambioEstado_PendienteDeCierre", idCambioEstado, "idPendienteDeCierre", idEstadoConcreto);
                break;
            case "AutoConfirmado":
                idEstadoConcreto = insertAutoConfirmado(c);
                insertRelacion(c, "CambioEstado_AutoConfirmado", idCambioEstado, "idAutoConfirmado", idEstadoConcreto);
                break;
        }
    }

    // Métodos para insertar estados concretos
    private long insertAutoDetectado(Connection c) throws SQLException {
        String sql = "INSERT INTO AutoDetectado (nombre) VALUES (?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "AutoDetectado");
            ps.executeUpdate();
        }
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No se generó ID para AutoDetectado");
        }
    }

    private long insertBloqueadoEnRevision(Connection c) throws SQLException {
        String sql = "INSERT INTO BloqueadoEnRevision (nombre) VALUES (?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "BloqueadoEnRevision");
            ps.executeUpdate();
        }
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No se generó ID para BloqueadoEnRevision");
        }
    }

    private long insertRechazado(Connection c) throws SQLException {
        String sql = "INSERT INTO Rechazado (nombre) VALUES (?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "Rechazado");
            ps.executeUpdate();
        }
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No se generó ID para Rechazado");
        }
    }

    private long insertDerivado(Connection c) throws SQLException {
        String sql = "INSERT INTO Derivado (nombre) VALUES (?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "Derivado");
            ps.executeUpdate();
        }
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No se generó ID para Derivado");
        }
    }

    private long insertConfirmadoPorPersonal(Connection c) throws SQLException {
        String sql = "INSERT INTO ConfirmadoPorPersonal (nombre) VALUES (?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "ConfirmadoPorPersonal");
            ps.executeUpdate();
        }
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No se generó ID para ConfirmadoPorPersonal");
        }
    }

    private long insertPendienteDeRevision(Connection c) throws SQLException {
        String sql = "INSERT INTO PendienteDeRevision (nombre) VALUES (?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "PendienteDeRevision");
            ps.executeUpdate();
        }
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No se generó ID para PendienteDeRevision");
        }
    }

    private long insertSinRevision(Connection c) throws SQLException {
        String sql = "INSERT INTO SinRevision (nombre) VALUES (?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "SinRevision");
            ps.executeUpdate();
        }
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No se generó ID para SinRevision");
        }
    }

    private long insertCerrado(Connection c) throws SQLException {
        String sql = "INSERT INTO Cerrado (nombre) VALUES (?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "Cerrado");
            ps.executeUpdate();
        }
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No se generó ID para Cerrado");
        }
    }

    private long insertPendienteDeCierre(Connection c) throws SQLException {
        String sql = "INSERT INTO PendienteDeCierre (nombre) VALUES (?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "PendienteDeCierre");
            ps.executeUpdate();
        }
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No se generó ID para PendienteDeCierre");
        }
    }

    private long insertAutoConfirmado(Connection c) throws SQLException {
        String sql = "INSERT INTO AutoConfirmado (nombre) VALUES (?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "AutoConfirmado");
            ps.executeUpdate();
        }
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No se generó ID para AutoConfirmado");
        }
    }

    /**
     * Inserta la relación en la tabla intermedia CambioEstado_*
     */
    private void insertRelacion(Connection c, String tablaIntermedia, long idCambioEstado, String columnaEstadoConcreto, long idEstadoConcreto) throws SQLException {
        String sql = "INSERT INTO " + tablaIntermedia + " (idCambioEstado, " + columnaEstadoConcreto + ") VALUES (?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idCambioEstado);
            ps.setLong(2, idEstadoConcreto);
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

    // Elimina un cambio por ID
    public void deleteById(long idCambioEstado) throws SQLException {
        Connection c = DatabaseConnection.getConnection();
        try {
            c.setAutoCommit(false);

            // Eliminar relaciones intermedias (intentar todas, no importa si no existen)
            deleteRelacionesIntermedias(c, idCambioEstado);

            // Eliminar de CambioEstado
            String sql = "DELETE FROM CambioEstado WHERE idCambioEstado = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, idCambioEstado);
                ps.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
            c.close();
        }
    }

    private void deleteRelacionesIntermedias(Connection c, long idCambioEstado) throws SQLException {
        String[] tablasIntermedias = {
            "CambioEstado_AutoDetectado",
            "CambioEstado_BloqueadoEnRevision",
            "CambioEstado_Rechazado",
            "CambioEstado_Derivado",
            "CambioEstado_ConfirmadoPorPersonal",
            "CambioEstado_PendienteDeRevision",
            "CambioEstado_Cerrado",
            "CambioEstado_PendienteDeCierre",
            "CambioEstado_AutoConfirmado"
        };

        for (String tabla : tablasIntermedias) {
            String sql = "DELETE FROM " + tabla + " WHERE idCambioEstado = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, idCambioEstado);
                ps.executeUpdate(); // No importa si no existe la relación
            } catch (SQLException e) {
                // Ignorar errores si la tabla no tiene registros
            }
        }
    }

    private CambioEstado map(ResultSet rs) throws SQLException {
        CambioEstado ce = new CambioEstado();
        ce.setIdCambioEstado(rs.getLong("idCambioEstado"));
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
        return ce;
    }

    private static String format(LocalDateTime dt) {
        return dt == null ? null : dt.toString().replace('T', ' ');
    }

    private static LocalDateTime parse(String s) {
        if (s == null) return null;
        return LocalDateTime.parse(s.replace(' ', 'T'));
    }
}

