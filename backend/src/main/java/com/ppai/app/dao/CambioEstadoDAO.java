package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class CambioEstadoDAO {

    private final EstadoDAO estadoDAO = new EstadoDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final MotivoFueraServicioDAO motivoFueraServicioDAO = new MotivoFueraServicioDAO();

    /* --------------------------------------------------------------
       INSERT – guarda datos principales + relaciones
       -------------------------------------------------------------- */
    public void insert(CambioEstado ce) throws SQLException {
        String sql = "INSERT INTO CambioEstado (fechaHoraInicio, fechaHoraFin, idEstado, idResponsableInspeccion) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, ce.getFechaHoraInicio());
            ps.setObject(2, ce.getFechaHoraFin());  // puede ser NULL
            ps.setLong  (3, ce.getEstado().getIdEstado());
            ps.setLong  (4, ce.getResponsableInspeccion().getIdEmpleado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idCambioEstado = rs.getLong(1);
                    ce.setIdCambioEstado(idCambioEstado);

                    // Persistir relación N:N con MotivoFueraServicio
                    insertMotivosFueraServicio(conn, idCambioEstado, ce.getMotivoFueraServicio());
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todo (incluyendo relaciones)
       -------------------------------------------------------------- */
    public void update(CambioEstado ce) throws SQLException {
        String sql = "UPDATE CambioEstado " +
                     "SET fechaHoraInicio = ?, fechaHoraFin = ?, idEstado = ?, idResponsableInspeccion = ? " +
                     "WHERE idCambioEstado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, ce.getFechaHoraInicio());
            ps.setObject(2, ce.getFechaHoraFin());
            ps.setLong  (3, ce.getEstado().getIdEstado());
            ps.setLong  (4, ce.getResponsableInspeccion().getIdEmpleado());
            ps.setLong  (5, ce.getIdCambioEstado());

            ps.executeUpdate();

            // Actualizar relación N:N
            deleteMotivosFueraServicio(conn, ce.getIdCambioEstado());
            insertMotivosFueraServicio(conn, ce.getIdCambioEstado(), ce.getMotivoFueraServicio());
        }
    }

    /* --------------------------------------------------------------
       DELETE – elimina registro + relaciones N:N
       -------------------------------------------------------------- */
    public void delete(long idCambioEstado) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            deleteMotivosFueraServicio(conn, idCambioEstado);

            String sql = "DELETE FROM CambioEstado WHERE idCambioEstado = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idCambioEstado);
                ps.executeUpdate();
            }
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga todo: estado, responsable, motivos
       -------------------------------------------------------------- */
    public CambioEstado findById(long idCambioEstado) throws SQLException {
        String sql = "SELECT * FROM CambioEstado WHERE idCambioEstado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idCambioEstado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CambioEstado ce = new CambioEstado();

                    ce.setIdCambioEstado(rs.getLong("idCambioEstado"));
                    ce.setFechaHoraInicio(getLocalDateTime(rs, "fechaHoraInicio"));
                    ce.setFechaHoraFin(getLocalDateTime(rs, "fechaHoraFin"));

                    // Cargar objetos relacionados
                    long idEstado = rs.getLong("idEstado");
                    Estado estado = estadoDAO.findById(idEstado);
                    ce.setEstado(estado);

                    long idEmpleado = rs.getLong("idResponsableInspeccion");
                    Empleado empleado = empleadoDAO.findById(idEmpleado);
                    ce.setResponsableInspeccion(empleado);

                    // Cargar motivos fuera de servicio
                    List<MotivoFueraServicio> motivos = findMotivosByCambioEstado(conn, idCambioEstado);
                    ce.setMotivoFueraServicio(motivos);

                    return ce;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa con todas las relaciones
       -------------------------------------------------------------- */
    public List<CambioEstado> findAll() throws SQLException {
        String sql = "SELECT * FROM CambioEstado";
        List<CambioEstado> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CambioEstado ce = new CambioEstado();

                ce.setIdCambioEstado(rs.getLong("idCambioEstado"));
                ce.setFechaHoraInicio(getLocalDateTime(rs, "fechaHoraInicio"));
                ce.setFechaHoraFin(getLocalDateTime(rs, "fechaHoraFin"));

                long idEstado = rs.getLong("idEstado");
                Estado estado = estadoDAO.findById(idEstado);
                ce.setEstado(estado);

                long idEmpleado = rs.getLong("idResponsableInspeccion");
                Empleado empleado = empleadoDAO.findById(idEmpleado);
                ce.setResponsableInspeccion(empleado);

                List<MotivoFueraServicio> motivos = findMotivosByCambioEstado(conn, ce.getIdCambioEstado());
                ce.setMotivoFueraServicio(motivos);

                list.add(ce);
            }
        }
        return list;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES PARA RELACIÓN N:N CON MotivoFueraServicio
    // ==============================================================

    private void insertMotivosFueraServicio(Connection conn, long idCambioEstado, List<MotivoFueraServicio> motivos) throws SQLException {
        String sql = "INSERT INTO CambioEstado_MotivoFueraServicio (idCambioEstado, idMotivoFueraServicio) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (MotivoFueraServicio m : motivos) {
                ps.setLong(1, idCambioEstado);
                ps.setLong(2, m.getIdMotivoFueraServicio());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteMotivosFueraServicio(Connection conn, long idCambioEstado) throws SQLException {
        String sql = "DELETE FROM CambioEstado_MotivoFueraServicio WHERE idCambioEstado = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idCambioEstado);
            ps.executeUpdate();
        }
    }

    private List<MotivoFueraServicio> findMotivosByCambioEstado(Connection conn, long idCambioEstado) throws SQLException {
        String sql = "SELECT m.* FROM MotivoFueraServicio m " +
                     "JOIN CambioEstado_MotivoFueraServicio cm ON m.idMotivoFueraServicio = cm.idMotivoFueraServicio " +
                     "WHERE cm.idCambioEstado = ?";
        List<MotivoFueraServicio> motivos = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idCambioEstado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Cargar el MotivoFueraServicio completo usando su propio DAO
                    long idMotivoFueraServicio = rs.getLong("idMotivoFueraServicio");
                    MotivoFueraServicio m = motivoFueraServicioDAO.findById(idMotivoFueraServicio);
                    if (m != null) {
                        motivos.add(m);
                    }
                }
            }
        }
        return motivos;
    }

    // ==============================================================
    // UTILIDAD: convertir Timestamp a LocalDateTime (maneja NULL)
    // ==============================================================
    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }

    // ==============================================================
    // Funcionalidad: Buscar Cambio de EStado por Sismografo
    // ==============================================================
    public List<CambioEstado> findBySismografoId(Connection conn, long idSismografo) throws SQLException {
        String sql = """
            SELECT ce.* FROM CambioEstado ce
            JOIN CambioEstado_Sismografo ces ON ce.idCambioEstado = ces.idCambioEstado
            WHERE ces.idSismografo = ?
            """;
        List<CambioEstado> cambios = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CambioEstado ce = new CambioEstado();

                    ce.setIdCambioEstado(rs.getLong("idCambioEstado"));
                    ce.setFechaHoraInicio(getLocalDateTime(rs, "fechaHoraInicio"));
                    ce.setFechaHoraFin(getLocalDateTime(rs, "fechaHoraFin"));

                    long idEstado = rs.getLong("idEstado");
                    Estado estado = estadoDAO.findById(idEstado);
                    ce.setEstado(estado);

                    long idEmpleado = rs.getLong("idResponsableInspeccion");
                    Empleado empleado = empleadoDAO.findById(idEmpleado);
                    ce.setResponsableInspeccion(empleado);

                    // Motivos fuera de servicio
                    List<MotivoFueraServicio> motivos = findMotivosByCambioEstado(conn, ce.getIdCambioEstado());
                    ce.setMotivoFueraServicio(motivos);

                    cambios.add(ce);
                }
            }
        }
        return cambios;
    }

    // ==============================================================
    // Funcionalidad: Buscar Cambios de Estado por Evento Sísmico
    // ==============================================================
    public List<CambioEstado> findByEventoSismicoId(Connection conn, long idEventoSismico) throws SQLException {
        String sql = """
            SELECT ce.* FROM CambioEstado ce
            JOIN EventoSismico_CambioEstado ece ON ce.idCambioEstado = ece.idCambioEstado
            WHERE ece.idEventoSismico = ?
            """;
        List<CambioEstado> cambios = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idEventoSismico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CambioEstado ce = new CambioEstado();

                    ce.setIdCambioEstado(rs.getLong("idCambioEstado"));
                    ce.setFechaHoraInicio(getLocalDateTime(rs, "fechaHoraInicio"));
                    ce.setFechaHoraFin(getLocalDateTime(rs, "fechaHoraFin"));

                    long idEstado = rs.getLong("idEstado");
                    Estado estado = estadoDAO.findById(idEstado);
                    ce.setEstado(estado);

                    long idEmpleado = rs.getLong("idResponsableInspeccion");
                    Empleado empleado = empleadoDAO.findById(idEmpleado);
                    ce.setResponsableInspeccion(empleado);

                    // Cargar motivos fuera de servicio
                    List<MotivoFueraServicio> motivos = findMotivosByCambioEstado(conn, ce.getIdCambioEstado());
                    ce.setMotivoFueraServicio(motivos);

                    cambios.add(ce);
                }
            }
        }
        return cambios;
    }
}