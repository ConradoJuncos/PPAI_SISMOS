package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para catálogo de estados de SerieTemporal (Activo, Inactivo).
 * Solo lectura, los estados se precargan en la BD.
 */
public class EstadoSerieTemporalDAO {

    public static class EstadoSerieTemporal {
        private long id;
        private String nombre;

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }

    public EstadoSerieTemporal findById(long id) throws SQLException {
        String sql = "SELECT * FROM EstadoSerieTemporal WHERE idEstadoSerieTemporal = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public EstadoSerieTemporal findByNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM EstadoSerieTemporal WHERE nombre = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<EstadoSerieTemporal> findAll() throws SQLException {
        String sql = "SELECT * FROM EstadoSerieTemporal";
        List<EstadoSerieTemporal> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private EstadoSerieTemporal map(ResultSet rs) throws SQLException {
        EstadoSerieTemporal e = new EstadoSerieTemporal();
        e.setId(rs.getLong("idEstadoSerieTemporal"));
        e.setNombre(rs.getString("nombre"));
        return e;
    }
}

