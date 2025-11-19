package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.EstacionSismologica;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EstacionSismologicaDAO {

    public EstacionSismologica findById(long id) throws SQLException {
        String sql = "SELECT * FROM EstacionSismologica WHERE idEstacionSismologica = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<EstacionSismologica> findAll() throws SQLException {
        String sql = "SELECT * FROM EstacionSismologica";
        List<EstacionSismologica> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void insert(EstacionSismologica e) throws SQLException {
        String sql = "INSERT INTO EstacionSismologica (idEstacionSismologica, documentoCertificacionAdq, fechaSolicitudCertificacion, latitud, longitud, nombre, nroCertificacionAdquisicion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, e.getCodigoEstacion());
            ps.setString(2, e.getDocumentoCertificacionAdq());
            ps.setString(3, format(e.getFechaSolicitudCertificacion()));
            ps.setDouble(4, e.getLatitud());
            ps.setDouble(5, e.getLongitud());
            ps.setString(6, e.getNombre());
            ps.setInt(7, e.getNroCertificacionAdquisicion());
            ps.executeUpdate();
        }
    }

    public void update(EstacionSismologica e) throws SQLException {
        String sql = "UPDATE EstacionSismologica SET documentoCertificacionAdq = ?, fechaSolicitudCertificacion = ?, latitud = ?, longitud = ?, nombre = ?, nroCertificacionAdquisicion = ? WHERE idEstacionSismologica = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getDocumentoCertificacionAdq());
            ps.setString(2, format(e.getFechaSolicitudCertificacion()));
            ps.setDouble(3, e.getLatitud());
            ps.setDouble(4, e.getLongitud());
            ps.setString(5, e.getNombre());
            ps.setInt(6, e.getNroCertificacionAdquisicion());
            ps.setLong(7, e.getCodigoEstacion());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM EstacionSismologica WHERE idEstacionSismologica = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private EstacionSismologica map(ResultSet rs) throws SQLException {
        EstacionSismologica e = new EstacionSismologica();
        e.setCodigoEstacion(rs.getLong("idEstacionSismologica"));
        e.setDocumentoCertificacionAdq(rs.getString("documentoCertificacionAdq"));
        String fecha = rs.getString("fechaSolicitudCertificacion");
        if (fecha != null) e.setFechaSolicitudCertificacion(parse(fecha));
        e.setLatitud(rs.getDouble("latitud"));
        e.setLongitud(rs.getDouble("longitud"));
        e.setNombre(rs.getString("nombre"));
        e.setNroCertificacionAdquisicion(rs.getInt("nroCertificacionAdquisicion"));
        return e;
    }

    private static String format(LocalDateTime dt) {
        return dt == null ? null : dt.toString().replace('T', ' ');
    }

    private static LocalDateTime parse(String s) {
        if (s == null) return null;
        return LocalDateTime.parse(s.replace(' ', 'T'));
    }
}

