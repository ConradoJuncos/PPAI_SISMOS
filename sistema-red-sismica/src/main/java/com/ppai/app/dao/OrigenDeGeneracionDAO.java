package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.OrigenDeGeneracion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrigenDeGeneracionDAO {

    public OrigenDeGeneracion findById(long id) throws SQLException {
        String sql = "SELECT * FROM OrigenDeGeneracion WHERE idOrigenDeGeneracion = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<OrigenDeGeneracion> findAll() throws SQLException {
        String sql = "SELECT * FROM OrigenDeGeneracion";
        List<OrigenDeGeneracion> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private OrigenDeGeneracion map(ResultSet rs) throws SQLException {
        OrigenDeGeneracion o = new OrigenDeGeneracion();
        o.setIdOrigenDeGeneracion(rs.getLong("idOrigenDeGeneracion"));
        o.setDescripcion(rs.getString("descripcion"));
        o.setNombre(rs.getString("nombre"));
        return o;
    }
}

