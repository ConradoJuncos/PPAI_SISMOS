package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.MuestraSismica;
import com.ppai.app.entidad.DetalleMuestraSismica;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MuestraSismicaDAO {

    private final DetalleMuestraSismicaDAO detalleDAO = new DetalleMuestraSismicaDAO();

    public List<MuestraSismica> findBySerieTemporalId(Connection c, long idSerie) throws SQLException {
        String sql = "SELECT * FROM MuestraSismica WHERE idSerieTemporal = ?";
        List<MuestraSismica> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idSerie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs, c));
            }
        }
        return list;
    }

    public List<MuestraSismica> findAll() throws SQLException {
        String sql = "SELECT * FROM MuestraSismica";
        List<MuestraSismica> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs, c));
        }
        return list;
    }

    public Map<Integer, Integer> getRelacionesSerieMuestra() throws SQLException {
        Map<Integer, Integer> relaciones = new HashMap<>();
        String sql = "SELECT idMuestraSismica, idSerieTemporal FROM MuestraSismica";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                relaciones.put(rs.getInt("idMuestraSismica"), rs.getInt("idSerieTemporal"));
            }
        }
        return relaciones;
    }

    private MuestraSismica map(ResultSet rs, Connection c) throws SQLException {
        MuestraSismica m = new MuestraSismica();
        m.setIdMuestraSismica(rs.getLong("idMuestraSismica"));
        String fecha = rs.getString("fechaHoraMuestraSismica");
        if (fecha != null) m.setFechaHoraMuestraSismica(LocalDateTime.parse(fecha.replace(" ", "T")));
        long id = m.getIdMuestraSismica();
        List<DetalleMuestraSismica> detalles = detalleDAO.findByMuestraId(c, id);
        m.setDetallesMuestraSismica(detalles);
        return m;
    }
}

