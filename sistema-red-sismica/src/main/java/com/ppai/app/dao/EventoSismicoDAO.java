package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventoSismicoDAO {

    private final ClasificacionSismoDAO clasificacionDAO = new ClasificacionSismoDAO();
    private final MagnitudRichterDAO magnitudDAO = new MagnitudRichterDAO();
    private final OrigenDeGeneracionDAO origenDAO = new OrigenDeGeneracionDAO();
    private final AlcanceSismoDAO alcanceDAO = new AlcanceSismoDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final SerieTemporalDAO serieTemporalDAO = new SerieTemporalDAO();
    private final CambioEstadoDAO cambioEstadoDAO = new CambioEstadoDAO();

    public EventoSismico findById(long id) throws SQLException {
        String sql = "SELECT * FROM EventoSismico WHERE idEventoSismico = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs, c);
            }
        }
        return null;
    }

    public List<EventoSismico> findAll() throws SQLException {
        String sql = "SELECT idEventoSismico FROM EventoSismico";
        List<EventoSismico> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                long id = rs.getLong("idEventoSismico");
                EventoSismico e = findById(id);
                if (e != null) list.add(e);
            }
        }
        return list;
    }

    public void update(EventoSismico e) throws SQLException {
        String sql = "UPDATE EventoSismico SET fechaHoraFin = ?, fechaHoraOcurrencia = ?, latitudEpicentro = ?, latitudHipocntro = ?, longitudEpicentro = ?, longitudHipocentro = ?, valorMagnitud = ?, idClasificacionSismo = ?, magnitudRichter = ?, idOrigenDeGeneracion = ?, idAlcanceSismo = ?, nombreEstadoActual = ?, idEmpleado = ? WHERE idEventoSismico = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, format(e.getFechaHoraFin()));
            ps.setString(2, format(e.getFechaHoraOcurrencia()));
            ps.setString(3, e.getLatitudEpicentro());
            ps.setString(4, e.getLatitudHipocentro());
            ps.setString(5, e.getLongitudEpicentro());
            ps.setString(6, e.getLongitudHipocentro());
            ps.setDouble(7, e.getValorMagnitud());
            ps.setLong(8, e.getClasificacionSismo().getIdClasificacionSismo());
            ps.setInt(9, e.getMagnitudRichter().getNumero());
            ps.setLong(10, e.getOrigenGeneracion().getOrigenDeGeneracion());
            ps.setLong(11, e.getAlcanceSismo().getIdAlcanceSismo());
            ps.setString(12, e.getEstadoActual() != null ? e.getEstadoActual().getNombreEstado() : null);
            if (e.getAnalistaSupervisor() != null) {
                ps.setLong(13, e.getAnalistaSupervisor().getIdEmpleado());
            } else {
                ps.setNull(13, Types.INTEGER);
            }
            ps.setLong(14, e.getIdEventoSismico());
            ps.executeUpdate();
        }
    }

    private static String format(LocalDateTime dt) {
        return dt == null ? null : dt.toString().replace('T', ' ');
    }

    private EventoSismico map(ResultSet rs, Connection c) throws SQLException {
        EventoSismico e = new EventoSismico();
        e.setIdEventoSismico(rs.getLong("idEventoSismico"));
        String fin = rs.getString("fechaHoraFin");
        String occ = rs.getString("fechaHoraOcurrencia");
        if (fin != null) e.setFechaHoraFin(LocalDateTime.parse(fin.replace(" ", "T")));
        if (occ != null) e.setFechaHoraOcurrencia(LocalDateTime.parse(occ.replace(" ", "T")));
        e.setLatitudEpicentro(rs.getString("latitudEpicentro"));
        e.setLatitudHipocentro(rs.getString("latitudHipocntro"));
        e.setLongitudEpicentro(rs.getString("longitudEpicentro"));
        e.setLongitudHipocentro(rs.getString("longitudHipocentro"));
        e.setValorMagnitud(rs.getDouble("valorMagnitud"));
        e.setClasificacionSismo(clasificacionDAO.findById(rs.getLong("idClasificacionSismo")));
        e.setMagnitudRichter(magnitudDAO.findByNumero(rs.getInt("magnitudRichter")));
        e.setOrigenDeGeneracion(origenDAO.findById(rs.getLong("idOrigenDeGeneracion")));
        e.setAlcanceSismo(alcanceDAO.findById(rs.getLong("idAlcanceSismo")));
        long idEmpleado = rs.getLong("idEmpleado");
        if (!rs.wasNull()) e.setAnalistaSupervisor(empleadoDAO.findById(idEmpleado));
        String nombreEstadoActual = rs.getString("nombreEstadoActual");
        e.setEstadoActual(EstadoFactory.crear(nombreEstadoActual));
        // Series temporales asociadas: buscar por FK directa (idEventoSismico)
        List<SerieTemporal> series = new ArrayList<>();
        String sqlSeries = "SELECT idSerieTemporal FROM SerieTemporal WHERE idEventoSismico = ?";
        try (PreparedStatement ps = c.prepareStatement(sqlSeries)) {
            ps.setLong(1, e.getIdEventoSismico());
            try (ResultSet rsSt = ps.executeQuery()) {
                while (rsSt.next()) {
                    long idSerie = rsSt.getLong("idSerieTemporal");
                    SerieTemporal stObj = serieTemporalDAO.findById(idSerie);
                    if (stObj != null) series.add(stObj);
                }
            }
        }
        e.setSeriesTemporales(new ArrayList<>(series));
        // Cargar historial de cambios de estado
        List<CambioEstado> cambios = cambioEstadoDAO.findByEvento(e.getIdEventoSismico());
        e.setCambioEstado(new ArrayList<>(cambios));
        return e;
    }
}

