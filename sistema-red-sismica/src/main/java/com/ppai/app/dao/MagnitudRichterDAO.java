package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.MagnitudRichter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MagnitudRichterDAO {

    public MagnitudRichter findByNumero(int numero) throws SQLException {
        String sql = "SELECT * FROM MagnitudRichter WHERE numero = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<MagnitudRichter> findAll() throws SQLException {
        String sql = "SELECT * FROM MagnitudRichter";
        List<MagnitudRichter> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private MagnitudRichter map(ResultSet rs) throws SQLException {
        MagnitudRichter m = new MagnitudRichter();
        m.setNumero(rs.getInt("numero"));
        m.setDescripcion(rs.getString("descripcion"));
        return m;
    }
}

