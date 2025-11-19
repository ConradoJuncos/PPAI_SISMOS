package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para catálogo de estados de Sismografo (Disponible, EnInstalacion, EnLinea, FueraDeServicio).
 * Solo lectura, los estados se precargan en la BD.
 */
public class EstadoSismografoDAO {

    public static class EstadoSismografo {
        private long id;
        private String nombre;

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }

    public EstadoSismografo findById(long id) throws SQLException {
        String sql = "SELECT * FROM EstadoSismografo WHERE idEstadoSismografo = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public EstadoSismografo findByNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM EstadoSismografo WHERE nombre = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<EstadoSismografo> findAll() throws SQLException {
        String sql = "SELECT * FROM EstadoSismografo";
        List<EstadoSismografo> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private EstadoSismografo map(ResultSet rs) throws SQLException {
        EstadoSismografo e = new EstadoSismografo();
        e.setId(rs.getLong("idEstadoSismografo"));
        e.setNombre(rs.getString("nombre"));
        return e;
    }
}

