package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.DetalleMuestraSismica;
import com.ppai.app.entidad.TipoDeDato;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetalleMuestraSismicaDAO {

    private final TipoDeDatoDAO tipoDeDatoDAO = new TipoDeDatoDAO();

    public List<DetalleMuestraSismica> findByMuestraId(Connection c, long idMuestra) throws SQLException {
        String sql = "SELECT * FROM DetalleMuestraSismica WHERE idMuestraSismica = ?";
        List<DetalleMuestraSismica> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idMuestra);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<DetalleMuestraSismica> findAll() throws SQLException {
        String sql = "SELECT * FROM DetalleMuestraSismica";
        List<DetalleMuestraSismica> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Map<Integer, Integer> getRelacionesMuestraDetalle() throws SQLException {
        Map<Integer, Integer> relaciones = new HashMap<>();
        String sql = "SELECT idDetalleMuestraSismica, idMuestraSismica FROM DetalleMuestraSismica";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                relaciones.put(rs.getInt("idDetalleMuestraSismica"), rs.getInt("idMuestraSismica"));
            }
        }
        return relaciones;
    }

    private DetalleMuestraSismica map(ResultSet rs) throws SQLException {
        DetalleMuestraSismica d = new DetalleMuestraSismica();
        d.setIdDetalleMuestraSismica(rs.getLong("idDetalleMuestraSismica"));
        d.setValor(rs.getDouble("valor"));
        long idTipo = rs.getLong("idTipoDeDato");
        TipoDeDato td = tipoDeDatoDAO.findById(idTipo);
        d.setTipoDeDato(td);
        return d;
    }
}

