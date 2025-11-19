package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.ClasificacionSismo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClasificacionSismoDAO {

    public ClasificacionSismo findById(long id) throws SQLException {
        String sql = "SELECT * FROM ClasificacionSismo WHERE idClasificacionSismo = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<ClasificacionSismo> findAll() throws SQLException {
        String sql = "SELECT * FROM ClasificacionSismo";
        List<ClasificacionSismo> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private ClasificacionSismo map(ResultSet rs) throws SQLException {
        ClasificacionSismo cs = new ClasificacionSismo();
        cs.setIdClasificacionSismo(rs.getLong("idClasificacionSismo"));
        cs.setKmProfundidadDesde(rs.getDouble("kmProfundidadDesde"));
        cs.setKmProfundidadHasta(rs.getDouble("kmProfundidadHasta"));
        cs.setNombre(rs.getString("nombre"));
        return cs;
    }
}

