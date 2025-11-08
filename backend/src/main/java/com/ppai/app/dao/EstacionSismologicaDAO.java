package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.EstacionSismologica;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class EstacionSismologicaDAO {

    /* --------------------------------------------------------------
       INSERT – guarda todos los campos, devuelve PK autogenerada
       -------------------------------------------------------------- */
    public void insert(EstacionSismologica e) throws SQLException {
        String sql = "INSERT INTO EstacionSismologica " +
                     "(documentoCertificacionAdq, fechaSolicitudCertificacion, latitud, longitud, nombre, nroCertificacionAdquisicion) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getDocumentoCertificacionAdq());
            ps.setObject(2, e.getFechaSolicitudCertificacion());  // <-- método sin "get"
            ps.setDouble(3, e.getLatitud());
            ps.setDouble(4, e.getLongitud());
            ps.setString(5, e.getNombre());
            ps.setInt   (6, e.getNroCertificacionAdquisicion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setCodigoEstacion(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todos los campos usando PK
       -------------------------------------------------------------- */
    public void update(EstacionSismologica e) throws SQLException {
        String sql = "UPDATE EstacionSismologica " +
                     "SET documentoCertificacionAdq = ?, fechaSolicitudCertificacion = ?, " +
                     "latitud = ?, longitud = ?, nombre = ?, nroCertificacionAdquisicion = ? " +
                     "WHERE codigoEstacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getDocumentoCertificacionAdq());
            ps.setObject(2, e.getFechaSolicitudCertificacion());
            ps.setDouble(3, e.getLatitud());
            ps.setDouble(4, e.getLongitud());
            ps.setString(5, e.getNombre());
            ps.setInt   (6, e.getNroCertificacionAdquisicion());
            ps.setLong  (7, e.getCodigoEstacion());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long codigoEstacion) throws SQLException {
        String sql = "DELETE FROM EstacionSismologica WHERE codigoEstacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, codigoEstacion);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve objeto completo
       -------------------------------------------------------------- */
    public EstacionSismologica findById(long codigoEstacion) throws SQLException {
        String sql = "SELECT * FROM EstacionSismologica WHERE codigoEstacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, codigoEstacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EstacionSismologica e = new EstacionSismologica();

                    e.setCodigoEstacion(rs.getLong("codigoEstacion"));
                    e.setDocumentoCertificacionAdq(rs.getString("documentoCertificacionAdq"));
                    e.setFechaSolicitudCertificacion(getLocalDateTime(rs, "fechaSolicitudCertificacion"));
                    e.setLatitud(rs.getDouble("latitud"));
                    e.setLongitud(rs.getDouble("longitud"));
                    e.setNombre(rs.getString("nombre"));
                    e.setNroCertificacionAdquisicion(rs.getInt("nroCertificacionAdquisicion"));

                    return e;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<EstacionSismologica> findAll() throws SQLException {
        String sql = "SELECT * FROM EstacionSismologica";
        List<EstacionSismologica> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EstacionSismologica e = new EstacionSismologica();

                e.setCodigoEstacion(rs.getLong("codigoEstacion"));
                e.setDocumentoCertificacionAdq(rs.getString("documentoCertificacionAdq"));
                e.setFechaSolicitudCertificacion(getLocalDateTime(rs, "fechaSolicitudCertificacion"));
                e.setLatitud(rs.getDouble("latitud"));
                e.setLongitud(rs.getDouble("longitud"));
                e.setNombre(rs.getString("nombre"));
                e.setNroCertificacionAdquisicion(rs.getInt("nroCertificacionAdquisicion"));

                list.add(e);
            }
        }
        return list;
    }

    // ==============================================================
    // UTILIDAD: convertir Timestamp a LocalDateTime (maneja NULL)
    // ==============================================================
    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}