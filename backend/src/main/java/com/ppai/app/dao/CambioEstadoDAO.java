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
       INSERT – guarda datos principales + relaciones. 
       -------------------------------------------------------------- */
    public void insert(CambioEstado ce) throws SQLException {
        // SQL corregido: usa la clave natural compuesta (ambitoEstado, nombreEstado)
        String sql = "INSERT INTO CambioEstado (fechaHoraInicio, fechaHoraFin, ambitoEstado, nombreEstado, idResponsableInspeccion) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, ce.getFechaHoraInicio());
            ps.setObject(2, ce.getFechaHoraFin());
            
            // Usamos la clave compuesta del Estado
            ps.setString(3, ce.getEstado().getAmbito());
            ps.setString(4, ce.getEstado().getNombreEstado());
            
            // idResponsableInspeccion puede ser NULL si no aplica
            ps.setObject(5, ce.getResponsableInspeccion() != null ? ce.getResponsableInspeccion().getIdEmpleado() : null);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idCambio = rs.getLong(1);
                    ce.setIdCambioEstado(idCambio);
                    
                    // Persistir relación N:N con MotivoFueraServicio
                    if (ce.getMotivoFueraServicio() != null && !ce.getMotivoFueraServicio().isEmpty()) {
                        insertMotivos(conn, idCambio, ce.getMotivoFueraServicio());
                    }
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza el cambio de estado.
       -------------------------------------------------------------- */
    public void update(CambioEstado ce) throws SQLException {
        String sql = "UPDATE CambioEstado SET fechaHoraInicio = ?, fechaHoraFin = ?, ambitoEstado = ?, nombreEstado = ?, idResponsableInspeccion = ? WHERE idCambioEstado = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, ce.getFechaHoraInicio());
            ps.setObject(2, ce.getFechaHoraFin());
            
            // Usamos la clave compuesta del Estado
            ps.setString(3, ce.getEstado().getAmbito());
            ps.setString(4, ce.getEstado().getNombreEstado());
            
            ps.setObject(5, ce.getResponsableInspeccion() != null ? ce.getResponsableInspeccion().getIdEmpleado() : null);
            ps.setLong  (6, ce.getIdCambioEstado());

            ps.executeUpdate();
            
            // Actualizar relación N:N con MotivoFueraServicio
            deleteMotivos(conn, ce.getIdCambioEstado());
            if (ce.getMotivoFueraServicio() != null && !ce.getMotivoFueraServicio().isEmpty()) {
                insertMotivos(conn, ce.getIdCambioEstado(), ce.getMotivoFueraServicio());
            }
        }
    }
    
    /* --------------------------------------------------------------
       DELETE – elimina el cambio de estado y sus relaciones
       -------------------------------------------------------------- */
    public void delete(long idCambioEstado) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Eliminar la relación N:N con MotivoFueraServicio
            deleteMotivos(conn, idCambioEstado);
            
            // 2. Eliminar el registro principal
            String sql = "DELETE FROM CambioEstado WHERE idCambioEstado = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idCambioEstado);
                ps.executeUpdate();
            }
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga todo
       -------------------------------------------------------------- */
    public CambioEstado findById(long idCambioEstado) throws SQLException {
        String sql = "SELECT * FROM CambioEstado WHERE idCambioEstado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idCambioEstado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCambioEstado(rs, conn);
                }
            }
        }
        return null;
    }

    // --- NUEVO MÉTODO SOLICITADO ---
    /* --------------------------------------------------------------
       FIND BY SISMOGRAFO ID – carga todos los cambios de estado para un sismógrafo.
       Se asume que la relación es 1:N (Sismografo:CambioEstado) gestionada por una tabla intermedia.
       -------------------------------------------------------------- */
    public List<CambioEstado> findBySismografoId(Connection conn, long idSismografo) throws SQLException {
        String sql = """
            SELECT ce.* FROM CambioEstado ce
            JOIN CambioEstado_Sismografo ces ON ce.idCambioEstado = ces.idCambioEstado
            WHERE ces.idSismografo = ?
            ORDER BY ce.fechaHoraInicio ASC
            """;
        List<CambioEstado> cambios = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Reutilizamos el mapeador, pasándole la conexión activa
                    cambios.add(mapResultSetToCambioEstado(rs, conn));
                }
            }
        }
        return cambios;
    }
    
    /* --------------------------------------------------------------
       FIND BY EVENTO SISMICO ID – carga todos los cambios de estado para un evento
       -------------------------------------------------------------- */
    public List<CambioEstado> findByEventoSismicoId(Connection conn, long idEventoSismico) throws SQLException {
        String sql = """
            SELECT ce.* FROM CambioEstado ce
            JOIN EventoSismico_CambioEstado ece ON ce.idCambioEstado = ece.idCambioEstado
            WHERE ece.idEventoSismico = ?
            ORDER BY ce.fechaHoraInicio ASC
            """;
        List<CambioEstado> cambios = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idEventoSismico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cambios.add(mapResultSetToCambioEstado(rs, conn));
                }
            }
        }
        return cambios;
    }
    
    // ==============================================================
    // MÉTODOS AUXILIARES
    // ==============================================================

    private CambioEstado mapResultSetToCambioEstado(ResultSet rs, Connection conn) throws SQLException {
        CambioEstado ce = new CambioEstado();

        ce.setIdCambioEstado(rs.getLong("idCambioEstado"));
        ce.setFechaHoraInicio(getLocalDateTime(rs, "fechaHoraInicio"));
        ce.setFechaHoraFin(getLocalDateTime(rs, "fechaHoraFin"));

        // Cargar Estado usando la CLAVE COMPUESTA
        String ambito = rs.getString("ambitoEstado");
        String nombreEstado = rs.getString("nombreEstado");
        Estado estado = estadoDAO.findByAmbitoAndNombre(ambito, nombreEstado);
        ce.setEstado(estado);

        // Cargar Empleado
        Long idEmpleado = rs.getObject("idResponsableInspeccion", Long.class);
        Empleado empleado = idEmpleado != null ? empleadoDAO.findById(idEmpleado) : null;
        ce.setResponsableInspeccion(empleado);

        // Cargar motivos fuera de servicio
        List<MotivoFueraServicio> motivos = findMotivosByCambioEstado(conn, ce.getIdCambioEstado());
        ce.setMotivoFueraServicio(motivos);

        return ce;
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES PARA RELACIÓN N:N CON MotivoFueraServicio
    // ==============================================================
    
    private void insertMotivos(Connection conn, long idCambioEstado, List<MotivoFueraServicio> motivos) throws SQLException {
        String sql = "INSERT INTO CambioEstado_MotivoFueraServicio (idCambioEstado, idMotivoFueraServicio) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (MotivoFueraServicio mfs : motivos) {
                ps.setLong(1, idCambioEstado);
                ps.setLong(2, mfs.getIdMotivoFueraServicio());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteMotivos(Connection conn, long idCambioEstado) throws SQLException {
        String sql = "DELETE FROM CambioEstado_MotivoFueraServicio WHERE idCambioEstado = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idCambioEstado);
            ps.executeUpdate();
        }
    }
    
    private List<MotivoFueraServicio> findMotivosByCambioEstado(Connection conn, long idCambioEstado) throws SQLException {
        String sql = """
            SELECT mfs.idMotivoFueraServicio FROM MotivoFueraServicio mfs
            JOIN CambioEstado_MotivoFueraServicio cemfs ON mfs.idMotivoFueraServicio = cemfs.idMotivoFueraServicio
            WHERE cemfs.idCambioEstado = ?
            """;
        List<MotivoFueraServicio> motivos = new ArrayList<>();
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idCambioEstado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long idMotivo = rs.getLong("idMotivoFueraServicio");
                    MotivoFueraServicio mfs = motivoFueraServicioDAO.findById(idMotivo); 
                    if (mfs != null) {
                        motivos.add(mfs);
                    }
                }
            }
        }
        return motivos;
    }
}