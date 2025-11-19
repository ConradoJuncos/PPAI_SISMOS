package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.AlcanceSismo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlcanceSismoDAO {

    public AlcanceSismo findById(long id) throws SQLException {
        String sql = "SELECT * FROM AlcanceSismo WHERE idAlcanceSismo = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<AlcanceSismo> findAll() throws SQLException {
        String sql = "SELECT * FROM AlcanceSismo";
        List<AlcanceSismo> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private AlcanceSismo map(ResultSet rs) throws SQLException {
        AlcanceSismo a = new AlcanceSismo();
        a.setIdAlcanceSismo(rs.getLong("idAlcanceSismo"));
        a.setNombre(rs.getString("nombre"));
        a.setDescripcion(rs.getString("descripcion"));
        return a;
    }
}

