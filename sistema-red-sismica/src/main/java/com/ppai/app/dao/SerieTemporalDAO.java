package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.SerieTemporal;
import com.ppai.app.entidad.Estado;
import com.ppai.app.entidad.MuestraSismica;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerieTemporalDAO {

    private final MuestraSismicaDAO muestraDAO = new MuestraSismicaDAO();

    public SerieTemporal findById(long id) throws SQLException {
        String sql = "SELECT st.*, est.nombre AS nombreEstado FROM SerieTemporal st JOIN EstadoSerieTemporal est ON st.idEstadoSerieTemporal = est.idEstadoSerieTemporal WHERE st.idSerieTemporal = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs, c);
            }
        }
        return null;
    }

    public List<SerieTemporal> findAll() throws SQLException {
        String sql = "SELECT st.idSerieTemporal FROM SerieTemporal st";
        List<SerieTemporal> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                long id = rs.getLong("idSerieTemporal");
                SerieTemporal serie = findById(id);
                if (serie != null) list.add(serie);
            }
        }
        return list;
    }

    private SerieTemporal map(ResultSet rs, Connection c) throws SQLException {
        SerieTemporal s = new SerieTemporal();
        s.setIdSerieTemporal(rs.getLong("idSerieTemporal"));
        s.setCondicionAlarma(rs.getString("condicionAlarma"));
        String fh = rs.getString("fechaHoraRegistro");
        if (fh != null) s.setFechaHoraRegistro(LocalDateTime.parse(fh.replace(" ", "T")));
        s.setFrecuenciaMuestreo(rs.getString("frecuenciaMuestreo"));
        String nombreEstado = rs.getString("nombreEstado");
        Estado estado = EstadoFactory.crear(nombreEstado);
        s.setEstado(estado);
        List<MuestraSismica> muestras = muestraDAO.findBySerieTemporalId(c, s.getIdSerieTemporal());
        s.setMuestrasSismicas(muestras);
        return s;
    }

    public Map<Integer, Integer> getRelacionesEventoSerie() throws SQLException {
        Map<Integer, Integer> relaciones = new HashMap<>();
        String sql = "SELECT idSerieTemporal, idEventoSismico FROM SerieTemporal";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int idSerie = rs.getInt("idSerieTemporal");
                int idEvento = rs.getInt("idEventoSismico");
                if (!rs.wasNull()) { // Solo agregar si idEventoSismico no es NULL
                    relaciones.put(idSerie, idEvento);
                }
            }
        }
        return relaciones;
    }
}

