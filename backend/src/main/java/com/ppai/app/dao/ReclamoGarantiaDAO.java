package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.ReclamoGarantia;
import com.ppai.app.entidad.Fabricante;
import com.ppai.app.entidad.Sismografo;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad ReclamoGarantia, corregido según la clase de entidad proporcionada.
 * Maneja las operaciones CRUD y sus relaciones 1:N con Fabricante y Sismografo.
 */
public class ReclamoGarantiaDAO {

    private final FabricanteDAO fabricanteDAO = new FabricanteDAO();
    private final SismografoDAO sismografoDAO = new SismografoDAO();
    
    // Nombres de la tabla y la PK corregidos
    private static final String TABLE_NAME = "ReclamoGarantia";
    private static final String PK_NAME = "nroReclamo";

    /* --------------------------------------------------------------
       INSERT – guarda datos principales + dependencias 1:N
       -------------------------------------------------------------- */
    public void insert(ReclamoGarantia r) throws SQLException {
        String sql = """
            INSERT INTO ReclamoGarantia 
            (comentario, fechaReclamo, fechaRespuesta, respuestaFabricante, idFabricante, idSismografo) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Campos directos
            ps.setString(1, r.getComentario());
            ps.setObject(2, r.getFechaReclamo());
            ps.setObject(3, r.getFechaRespuesta());
            ps.setString(4, r.getRespuestaFabricante());
            
            // Claves foráneas (FKs)
            // Asumimos que Fabricante y Sismografo tienen un getIdXyz() que retorna su PK
            ps.setLong(5, r.getFabricante().getIdFabricante()); 
            ps.setLong(6, r.getSismografo().getIdentificadorSismografo()); 

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long nroReclamo = rs.getLong(1);
                    r.setNroReclamo(nroReclamo);
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todos los datos
       -------------------------------------------------------------- */
    public void update(ReclamoGarantia r) throws SQLException {
        String sql = """
            UPDATE ReclamoGarantia SET 
            comentario = ?, fechaReclamo = ?, fechaRespuesta = ?, respuestaFabricante = ?, 
            idFabricante = ?, idSismografo = ?
            WHERE nroReclamo = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getComentario());
            ps.setObject(2, r.getFechaReclamo());
            ps.setObject(3, r.getFechaRespuesta());
            ps.setString(4, r.getRespuestaFabricante());
            ps.setLong(5, r.getFabricante().getIdFabricante());
            ps.setLong(6, r.getSismografo().getIdentificadorSismografo());
            ps.setLong(7, r.getNroReclamo()); // PK en la condición WHERE

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – elimina registro
       -------------------------------------------------------------- */
    public void delete(long nroReclamo) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + PK_NAME + " = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, nroReclamo);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga todas las relaciones
       -------------------------------------------------------------- */
    public ReclamoGarantia findByNroReclamo(long nroReclamo) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + PK_NAME + " = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, nroReclamo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReclamo(rs);
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<ReclamoGarantia> findAll() throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<ReclamoGarantia> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToReclamo(rs));
            }
        }
        return list;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES
    // ==============================================================

    private ReclamoGarantia mapResultSetToReclamo(ResultSet rs) throws SQLException {
        ReclamoGarantia r = new ReclamoGarantia();
        
        // Mapeo de campos directos (corregidos)
        long nroReclamo = rs.getLong(PK_NAME);
        r.setNroReclamo(nroReclamo);
        
        r.setComentario(rs.getString("comentario"));
        r.setFechaReclamo(getLocalDateTime(rs, "fechaReclamo"));
        r.setFechaRespuesta(getLocalDateTime(rs, "fechaRespuesta"));
        r.setRespuestaFabricante(rs.getString("respuestaFabricante"));

        // Cargar Fabricante (1:N)
        long idFabricante = rs.getLong("idFabricante");
        Fabricante fab = fabricanteDAO.findById(idFabricante);
        r.setFabricante(fab);

        // Cargar Sismografo (1:N)
        long idSismografo = rs.getLong("idSismografo");
        Sismografo s = sismografoDAO.findById(idSismografo);
        r.setSismografo(s);
        
        // NO se incluye el Estado, ya que no existe en la clase de entidad.
        
        return r;
    }
    
    /**
     * Convierte un campo de texto de la base de datos a LocalDateTime (maneja NULL).
     */
    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}