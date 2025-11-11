package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TareaAsignadaDAO {

    private final TipoTareaInspeccionDAO tipoTareaDAO = new TipoTareaInspeccionDAO();
    private final ApreciacionTipoDAO apreciacionDAO = new ApreciacionTipoDAO();

    /* --------------------------------------------------------------
       INSERT – REQUIERE que t.getTarea() esté cargado (con ID)
       -------------------------------------------------------------- */
    public void insert(TareaAsignada t) throws SQLException {
        if (t.getTarea() == null || t.getTarea().getCodigo() == 0) {
            throw new IllegalArgumentException("La tarea debe tener un TipoTareaInspeccion válido con ID");
        }

        // Esta versión sin FK 'numeroOrden' asume que la TareaAsignada existe por sí misma
        // (lo cual es raro, pero mantenemos por si es necesario para otros flujos).
        String sql = """
            INSERT INTO TareaAsignada 
            (comentario, fechaHoraRealizacion, idTipoTareaInspeccion)
            VALUES (?, ?, ?)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getComentario());
            ps.setObject(2, t.getFechaHoraRealizacion());
            ps.setLong  (3, t.getTarea().getCodigo()); 

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idTarea = rs.getLong(1);
                    t.setIdTareaAsignada(idTarea);

                    insertApreciaciones(conn, idTarea, t.getApreciacion());
                }
            }
        }
    }
    
    /* --------------------------------------------------------------
       INSERT FOR ORDEN – para colecciones 1:N (la FK de Orden va aquí)
       -------------------------------------------------------------- */
    public void insertForOrden(Connection conn, long numeroOrden, TareaAsignada t) throws SQLException {
        if (t.getTarea() == null || t.getTarea().getCodigo() == 0) {
            throw new IllegalArgumentException("La tarea debe tener un TipoTareaInspeccion válido con ID");
        }

        // SQL corregido para incluir la clave foránea de OrdenDeInspeccion
        String sql = """
            INSERT INTO TareaAsignada 
            (comentario, fechaHoraRealizacion, idTipoTareaInspeccion, numeroOrden)
            VALUES (?, ?, ?, ?)
            """;
        
        // Usamos la conexión proporcionada
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 

            ps.setString(1, t.getComentario());
            ps.setObject(2, t.getFechaHoraRealizacion());
            ps.setLong  (3, t.getTarea().getCodigo());
            ps.setLong  (4, numeroOrden); // <-- CLAVE FORÁNEA

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idTarea = rs.getLong(1);
                    t.setIdTareaAsignada(idTarea);

                    // Persistir relaciones N:N dentro de la misma conexión transaccional
                    insertApreciaciones(conn, idTarea, t.getApreciacion());
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – igual, requiere objeto cargado
       -------------------------------------------------------------- */
    public void update(TareaAsignada t) throws SQLException {
        if (t.getTarea() == null || t.getTarea().getCodigo() == 0) {
            throw new IllegalArgumentException("La tarea debe tener un TipoTareaInspeccion válido con ID");
        }

        String sql = """
            UPDATE TareaAsignada SET 
            comentario = ?, fechaHoraRealizacion = ?, idTipoTareaInspeccion = ?
            WHERE idTareaAsignada = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getComentario());
            ps.setObject(2, t.getFechaHoraRealizacion());
            ps.setLong  (3, t.getTarea().getCodigo());
            ps.setLong  (4, t.getIdTareaAsignada());

            ps.executeUpdate();

            deleteApreciaciones(conn, t.getIdTareaAsignada());
            insertApreciaciones(conn, t.getIdTareaAsignada(), t.getApreciacion());
        }
    }
    
    /* --------------------------------------------------------------
       DELETE – elimina registro por PK
       -------------------------------------------------------------- */
    public void delete(long idTareaAsignada) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            deleteApreciaciones(conn, idTareaAsignada);
            
            String sql = "DELETE FROM TareaAsignada WHERE idTareaAsignada = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idTareaAsignada);
                ps.executeUpdate();
            }
        }
    }
    
    /* --------------------------------------------------------------
       DELETE BY ORDEN ID – elimina todas las tareas de una orden
       -------------------------------------------------------------- */
    public void deleteByOrdenId(Connection conn, long numeroOrden) throws SQLException {
        // Primero, encontrar todas las tareas para borrar sus apreciaciones
        List<TareaAsignada> tareas = findByOrdenId(conn, numeroOrden);
        for (TareaAsignada t : tareas) {
            deleteApreciaciones(conn, t.getIdTareaAsignada());
        }
        
        // Luego, borrar las tareas
        String sql = "DELETE FROM TareaAsignada WHERE numeroOrden = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, numeroOrden);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga el objeto TipoTareaInspeccion completo
       -------------------------------------------------------------- */
    public TareaAsignada findById(long idTareaAsignada) throws SQLException {
        String sql = "SELECT * FROM TareaAsignada WHERE idTareaAsignada = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idTareaAsignada);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Usamos el mapeador y una nueva conexión
                    return mapResultSetToTareaAsignada(rs, conn); 
                }
            }
        }
        return null;
    }
    
    /* --------------------------------------------------------------
       FIND BY ORDEN ID – para cargar colecciones 1:N
       -------------------------------------------------------------- */
    public List<TareaAsignada> findByOrdenId(Connection conn, long numeroOrden) throws SQLException {
        String sql = "SELECT * FROM TareaAsignada WHERE numeroOrden = ?";
        List<TareaAsignada> tareas = new ArrayList<>();
        // Usamos la conexión proporcionada
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, numeroOrden);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Mapeo que reutiliza la lógica y la conexión transaccional
                    tareas.add(mapResultSetToTareaAsignada(rs, conn)); 
                }
            }
        }
        return tareas;
    }
    
    // ... findAll() igual, con carga completa ...

    // ==============================================================
    // MÉTODOS AUXILIARES
    // ==============================================================
    
    /**
     * Mapea un ResultSet a un objeto TareaAsignada, cargando sus dependencias.
     * Requiere una conexión para cargar la colección ApreciacionTipo.
     */
    private TareaAsignada mapResultSetToTareaAsignada(ResultSet rs, Connection conn) throws SQLException {
        TareaAsignada t = new TareaAsignada();

        long idTareaAsignada = rs.getLong("idTareaAsignada");
        t.setIdTareaAsignada(idTareaAsignada);
        t.setComentario(rs.getString("comentario"));
        t.setFechaHoraRealizacion(getLocalDateTime(rs, "fechaHoraRealizacion"));

        // CARGAR OBJETO COMPLETO
        long idTipoTarea = rs.getLong("idTipoTareaInspeccion");
        TipoTareaInspeccion tipo = tipoTareaDAO.findById(idTipoTarea);
        t.setTarea(tipo);

        // Cargar apreciaciones (requiere la conexión para el DAO)
        List<ApreciacionTipo> apreciaciones = apreciacionDAO.findByTareaAsignadaId(conn, idTareaAsignada);
        t.setApreciacion(apreciaciones);
        
        return t;
    }

    private void insertApreciaciones(Connection conn, long idTarea, List<ApreciacionTipo> apreciaciones) throws SQLException {
        String sql = "INSERT INTO TareaAsignada_Apreciacion (idTareaAsignada, idApreciacion) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (ApreciacionTipo a : apreciaciones) {
                if (a.getIdApreciacionTipo() == 0) {
                    throw new IllegalArgumentException("ApreciacionTipo debe tener ID");
                }
                ps.setLong(1, idTarea);
                ps.setLong(2, a.getIdApreciacionTipo());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteApreciaciones(Connection conn, long idTarea) throws SQLException {
        String sql = "DELETE FROM TareaAsignada_Apreciacion WHERE idTareaAsignada = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTarea);
            ps.executeUpdate();
        }
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        // Se mantiene la implementación original usando Timestamp para evitar nuevas inconsistencias
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}