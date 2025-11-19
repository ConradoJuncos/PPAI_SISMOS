package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.TipoDeDato;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoDeDatoDAO {

    public TipoDeDato findById(long id) throws SQLException {
        String sql = "SELECT * FROM TipoDeDato WHERE idTipoDeDato = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<TipoDeDato> findAll() throws SQLException {
        String sql = "SELECT * FROM TipoDeDato";
        List<TipoDeDato> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private TipoDeDato map(ResultSet rs) throws SQLException {
        TipoDeDato td = new TipoDeDato();
        td.setIdTipoDeDato(rs.getLong("idTipoDeDato"));
        td.setDenominacion(rs.getString("denominacion"));
        td.setnombreUnidadMedida(rs.getString("nombreUnidadMedida"));
        td.setValorUmbral(rs.getDouble("valorUmbral"));
        return td;
    }
}

