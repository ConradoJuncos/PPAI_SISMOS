package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Sismografo;
import com.ppai.app.entidad.EstacionSismologica;
import com.ppai.app.entidad.SerieTemporal;
import com.ppai.app.entidad.Estado;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SismografoDAO {

    private final EstadoSismografoDAO estadoSismografoDAO = new EstadoSismografoDAO();
    private final EstacionSismologicaDAO estacionDAO = new EstacionSismologicaDAO();
    private final SerieTemporalDAO serieDAO = new SerieTemporalDAO();

    public Sismografo findById(long id) throws SQLException {
        String sql = "SELECT * FROM Sismografo WHERE identificadorSismografo = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs, c);
            }
        }
        return null;
    }

    public List<Sismografo> findAll() throws SQLException {
        String sql = "SELECT identificadorSismografo FROM Sismografo";
        List<Sismografo> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                long id = rs.getLong("identificadorSismografo");
                Sismografo s = findById(id);
                if (s != null) list.add(s);
            }
        }
        return list;
    }

    public void insert(Sismografo s) throws SQLException {
        String sql = "INSERT INTO Sismografo (identificadorSismografo, fechaAdquisicion, nroSerie, idEstadoSismografo, idEstacionSismologica) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, s.getIdentificadorSismografo());
            ps.setString(2, format(s.getFechaAdquisicion()));
            ps.setLong(3, s.getNroSerie());
            // Nota: Sismografo.getEstadoActual() devuelve Estado, necesitamos extraer id del estado del sismógrafo
            // Para simplificar, asumimos que pasamos un id válido. Ajustar según implementación.
            ps.setNull(4, Types.INTEGER); // TODO: Implementar extracción de idEstadoSismografo desde Estado
            ps.setLong(5, s.getEstacionSismologica().getCodigoEstacion());
            ps.executeUpdate();
        }
    }

    public void update(Sismografo s) throws SQLException {
        String sql = "UPDATE Sismografo SET fechaAdquisicion = ?, nroSerie = ?, idEstadoSismografo = ?, idEstacionSismologica = ? WHERE identificadorSismografo = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, format(s.getFechaAdquisicion()));
            ps.setLong(2, s.getNroSerie());
            ps.setNull(3, Types.INTEGER); // TODO: Implementar extracción de idEstadoSismografo
            ps.setLong(4, s.getEstacionSismologica().getCodigoEstacion());
            ps.setLong(5, s.getIdentificadorSismografo());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM Sismografo WHERE identificadorSismografo = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Sismografo map(ResultSet rs, Connection c) throws SQLException {
        Sismografo s = new Sismografo();
        s.setIdentificadorSismografo(rs.getLong("identificadorSismografo"));
        String fecha = rs.getString("fechaAdquisicion");
        if (fecha != null) s.setFechaAdquisicion(parse(fecha));
        s.setNroSerie(rs.getLong("nroSerie"));

        // Cargar estado del sismógrafo
        long idEstado = rs.getLong("idEstadoSismografo");
        if (!rs.wasNull()) {
            EstadoSismografoDAO.EstadoSismografo estadoSismo = estadoSismografoDAO.findById(idEstado);
            // Nota: Sismografo.estadoActual es de tipo Estado (del patrón State para eventos).
            // Aquí necesitamos un estado de sismógrafo que es diferente.
            // Por ahora dejamos null; requiere ajuste en el diseño de entidades.
            // s.setEstadoActual(...); // TODO: Ajustar según diseño
        }

        // Cargar estación sismológica
        long idEstacion = rs.getLong("idEstacionSismologica");
        EstacionSismologica est = estacionDAO.findById(idEstacion);
        s.setEstacionSismologica(est);

        // Cargar series temporales asociadas a este sismógrafo
        List<SerieTemporal> series = findSeriesBySismografo(c, s.getIdentificadorSismografo());
        s.setSerieTemporal(series);

        // Nota: modelo y cambioEstado no se cargan aquí porque:
        // - modelo: La tabla Sismografo no tiene FK a ModeloSismografo en el esquema actual
        // - cambioEstado: Se manejaría con CambioEstadoSismografoDAO

        return s;
    }

    private List<SerieTemporal> findSeriesBySismografo(Connection c, long idSismografo) throws SQLException {
        String sql = "SELECT idSerieTemporal FROM SerieTemporal WHERE idSismografo = ?";
        List<SerieTemporal> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long idSerie = rs.getLong("idSerieTemporal");
                    SerieTemporal st = serieDAO.findById(idSerie);
                    if (st != null) list.add(st);
                }
            }
        }
        return list;
    }

    private static String format(LocalDateTime dt) {
        return dt == null ? null : dt.toString().replace('T', ' ');
    }

    private static LocalDateTime parse(String s) {
        if (s == null) return null;
        return LocalDateTime.parse(s.replace(' ', 'T'));
    }
}

