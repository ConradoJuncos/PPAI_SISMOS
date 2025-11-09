package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Suscripcion;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Suscripcion.
 * Maneja las operaciones CRUD.
 * La entidad Suscripcion solo tiene campos propios y no tiene relaciones 1:N que deba cargar,
 * su relación N:N con Usuario se gestiona desde UsuarioDAO.
 */
public class SuscripcionDAO {

    /* --------------------------------------------------------------
       INSERT – guarda fechas, devuelve PK autogenerada
       -------------------------------------------------------------- */
    public void insert(Suscripcion s) throws SQLException {
        // En la base de datos SQLite, las fechas/horas se almacenan como TEXT (ISO 8601) 
        String sql = "INSERT INTO Suscripcion (fechaHoraFinSuscripcion, fechaHoraInicioSuscripcion) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Utilizamos setObject para enviar LocalDateTime (se mapea a TEXT en SQLite)
            ps.setObject(1, s.getFechaHoraFinSuscripcion());    // puede ser NULL
            ps.setObject(2, s.getFechaHoraInicioSuscripcion()); // puede ser NULL
            
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    s.setIdSuscripcion(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza fechas
       -------------------------------------------------------------- */
    public void update(Suscripcion s) throws SQLException {
        String sql = "UPDATE Suscripcion SET fechaHoraFinSuscripcion = ?, fechaHoraInicioSuscripcion = ? WHERE idSuscripcion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, s.getFechaHoraFinSuscripcion());
            ps.setObject(2, s.getFechaHoraInicioSuscripcion());
            ps.setLong  (3, s.getIdSuscripcion());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idSuscripcion) throws SQLException {
        String sql = "DELETE FROM Suscripcion WHERE idSuscripcion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, idSuscripcion);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve objeto completo
       -------------------------------------------------------------- */
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

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<Suscripcion> findAll() throws SQLException {
        String sql = "SELECT * FROM Suscripcion";
        List<Suscripcion> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToSuscripcion(rs));
            }
        }
        return list;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES
    // ==============================================================
    
    /**
     * Mapea un ResultSet a un objeto Suscripcion.
     */
    private Suscripcion mapResultSetToSuscripcion(ResultSet rs) throws SQLException {
        Suscripcion s = new Suscripcion();
        s.setIdSuscripcion(rs.getLong("idSuscripcion"));
        
        // Convertir TEXT (ISO 8601) de la BD a LocalDateTime de Java
        s.setFechaHoraFinSuscripcion(getLocalDateTime(rs, "fechaHoraFinSuscripcion"));
        s.setFechaHoraInicioSuscripcion(getLocalDateTime(rs, "fechaHoraInicioSuscripcion"));
        
        return s;
    }

    /**
     * Convierte un campo de texto de la base de datos a LocalDateTime (maneja NULL).
     * Se usa para campos de fecha y hora.
     */
    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        // SQLite almacena LocalDateTime como TEXT, por lo que leemos como String
        String dateTimeStr = rs.getString(column);
        // Si no es nulo, parseamos
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr) : null;
    }

    // ==============================================================
    // FUNCIÓN NECESARIA PARA UsuarioDAO
    // ==============================================================
    // Esta función permite que UsuarioDAO pueda cargar las suscripciones de un usuario.
    public List<Suscripcion> findByUsuarioId(Connection conn, long idUsuario) throws SQLException {
        String sql = "SELECT s.* FROM Suscripcion s " +
                     "JOIN Usuario_Suscripcion us ON s.idSuscripcion = us.idSuscripcion " +
                     "WHERE us.idUsuario = ?";
        List<Suscripcion> suscripciones = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suscripciones.add(mapResultSetToSuscripcion(rs));
                }
            }
        }
        return suscripciones;
    }
}