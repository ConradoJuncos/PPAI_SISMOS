package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Reparacion;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReparacionDAO {

    /* --------------------------------------------------------------
       INSERT – guarda todos los campos, devuelve PK autogenerada
       -------------------------------------------------------------- */
    public void insert(Reparacion r) throws SQLException {
        String sql = "INSERT INTO Reparacion " +
                     "(comentarioReparacion, comentarioSolucion, fechaEnvioReparacion, fechaRespuestaReparacion) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getComentarioReparacion());
            ps.setString(2, r.getComentarioSolucion());
            ps.setObject(3, r.getFechaEnvioReparacion());
            ps.setObject(4, r.getFechaRespuestaReparacion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setNroReparacion(rs.getInt(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todos los campos usando PK
       -------------------------------------------------------------- */
    public void update(Reparacion r) throws SQLException {
        String sql = "UPDATE Reparacion " +
                     "SET comentarioReparacion = ?, comentarioSolucion = ?, " +
                     "fechaEnvioReparacion = ?, fechaRespuestaReparacion = ? " +
                     "WHERE nroReparacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getComentarioReparacion());
            ps.setString(2, r.getComentarioSolucion());
            ps.setObject(3, r.getFechaEnvioReparacion());
            ps.setObject(4, r.getFechaRespuestaReparacion());
            ps.setInt   (5, r.getNroReparacion());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(int nroReparacion) throws SQLException {
        String sql = "DELETE FROM Reparacion WHERE nroReparacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nroReparacion);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve objeto completo
       -------------------------------------------------------------- */
    public Reparacion findById(int nroReparacion) throws SQLException {
        String sql = "SELECT * FROM Reparacion WHERE nroReparacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nroReparacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Reparacion r = new Reparacion();

                    r.setNroReparacion(rs.getInt("nroReparacion"));
                    r.setComentarioReparacion(rs.getString("comentarioReparacion"));
                    r.setComentarioSolucion(rs.getString("comentarioSolucion"));
                    r.setFechaEnvioReparacion(getLocalDateTime(rs, "fechaEnvioReparacion"));
                    r.setFechaRespuestaReparacion(getLocalDateTime(rs, "fechaRespuestaReparacion"));

                    return r;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<Reparacion> findAll() throws SQLException {
        String sql = "SELECT * FROM Reparacion";
        List<Reparacion> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reparacion r = new Reparacion();

                r.setNroReparacion(rs.getInt("nroReparacion"));
                r.setComentarioReparacion(rs.getString("comentarioReparacion"));
                r.setComentarioSolucion(rs.getString("comentarioSolucion"));
                r.setFechaEnvioReparacion(getLocalDateTime(rs, "fechaEnvioReparacion"));
                r.setFechaRespuestaReparacion(getLocalDateTime(rs, "fechaRespuestaReparacion"));

                list.add(r);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       Funcionalidad: Buscar reparaciones por Sismógrafo (para SismografoDAO)
       -------------------------------------------------------------- */
    public List<Reparacion> findBySismografoId(Connection conn, long idSismografo) throws SQLException {
        String sql = """
            SELECT r.* FROM Reparacion r
            JOIN Reparacion_Sismografo rs ON r.nroReparacion = rs.nroReparacion
            WHERE rs.identificadorSismografo = ?
            """;
        List<Reparacion> reparaciones = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reparacion r = new Reparacion();

                    r.setNroReparacion(rs.getInt("nroReparacion"));
                    r.setComentarioReparacion(rs.getString("comentarioReparacion"));
                    r.setComentarioSolucion(rs.getString("comentarioSolucion"));
                    r.setFechaEnvioReparacion(getLocalDateTime(rs, "fechaEnvioReparacion"));
                    r.setFechaRespuestaReparacion(getLocalDateTime(rs, "fechaRespuestaReparacion"));

                    reparaciones.add(r);
                }
            }
        }
        return reparaciones;
    }

    // ==============================================================
    // UTILIDAD: convertir Timestamp a LocalDateTime (maneja NULL)
    // ==============================================================
    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}