package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class SismografoDAO {

    private final EstacionSismologicaDAO estacionDAO = new EstacionSismologicaDAO();
    private final ModeloSismografoDAO modeloDAO = new ModeloSismografoDAO();
    private final CambioEstadoDAO cambioEstadoDAO = new CambioEstadoDAO();
    private final ReparacionDAO reparacionDAO = new ReparacionDAO(); // ← Asumimos que existe

    /* --------------------------------------------------------------
       INSERT – guarda datos principales + relaciones
       -------------------------------------------------------------- */
    public void insert(Sismografo s) throws SQLException {
        String sql = "INSERT INTO Sismografo " +
                     "(fechaAdquisicion, nroSerie, codigoEstacion, idModelo) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, s.getFechaAdquisicion());
            ps.setLong  (2, s.getNroSerie());
            ps.setLong  (3, s.getEstacionSismologica().getCodigoEstacion());
            ps.setLong  (4, s.getModelo().getIdModeloSismografo());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idSismografo = rs.getLong(1);
                    s.setIdentificadorSismografo(idSismografo);

                    // Persistir relaciones 1:N
                    insertCambioEstado(conn, idSismografo, s.getCambioEstado());
                    insertReparacion(conn, idSismografo, s.getReparacion());
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todo (incluyendo relaciones)
       -------------------------------------------------------------- */
    public void update(Sismografo s) throws SQLException {
        String sql = "UPDATE Sismografo " +
                     "SET fechaAdquisicion = ?, nroSerie = ?, codigoEstacion = ?, idModelo = ? " +
                     "WHERE identificadorSismografo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, s.getFechaAdquisicion());
            ps.setLong  (2, s.getNroSerie());
            ps.setLong  (3, s.getEstacionSismologica().getCodigoEstacion());
            ps.setLong  (4, s.getModelo().getIdModeloSismografo());
            ps.setLong  (5, s.getIdentificadorSismografo());

            ps.executeUpdate();

            // Actualizar relaciones 1:N
            deleteCambioEstado(conn, s.getIdentificadorSismografo());
            deleteReparacion(conn, s.getIdentificadorSismografo());
            insertCambioEstado(conn, s.getIdentificadorSismografo(), s.getCambioEstado());
            insertReparacion(conn, s.getIdentificadorSismografo(), s.getReparacion());
        }
    }

    /* --------------------------------------------------------------
       DELETE – elimina registro + relaciones
       -------------------------------------------------------------- */
    public void delete(long identificadorSismografo) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            deleteCambioEstado(conn, identificadorSismografo);
            deleteReparacion(conn, identificadorSismografo);

            String sql = "DELETE FROM Sismografo WHERE identificadorSismografo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, identificadorSismografo);
                ps.executeUpdate();
            }
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga todo: estación, modelo, cambios, reparaciones
       -------------------------------------------------------------- */
    public Sismografo findById(long identificadorSismografo) throws SQLException {
        String sql = "SELECT * FROM Sismografo WHERE identificadorSismografo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, identificadorSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sismografo s = new Sismografo();

                    s.setIdentificadorSismografo(rs.getLong("identificadorSismografo"));
                    s.setFechaAdquisicion(getLocalDateTime(rs, "fechaAdquisicion"));
                    s.setNroSerie(rs.getLong("nroSerie"));

                    // Cargar objetos relacionados
                    long codigoEstacion = rs.getLong("codigoEstacion");
                    EstacionSismologica estacion = estacionDAO.findById(codigoEstacion);
                    s.setEstacionSismologica(estacion);

                    long idModelo = rs.getLong("idModelo");
                    ModeloSismografo modelo = modeloDAO.findById(idModelo);
                    s.setModelo(modelo);

                    // Cargar listas 1:N
                    List<CambioEstado> cambios = cambioEstadoDAO.findBySismografoId(conn, identificadorSismografo);
                    s.setCambioEstado(cambios);

                    List<Reparacion> reparaciones = reparacionDAO.findBySismografoId(conn, identificadorSismografo);
                    s.setReparacion(reparaciones);

                    // Estado actual: último cambio con fechaHoraFin = null
                    Estado estadoActual = findEstadoActual(conn, identificadorSismografo);
                    s.setEstadoActual(estadoActual);

                    return s;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa con todas las relaciones
       -------------------------------------------------------------- */
    public List<Sismografo> findAll() throws SQLException {
        String sql = "SELECT * FROM Sismografo";
        List<Sismografo> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sismografo s = new Sismografo();

                s.setIdentificadorSismografo(rs.getLong("identificadorSismografo"));
                s.setFechaAdquisicion(getLocalDateTime(rs, "fechaAdquisicion"));
                s.setNroSerie(rs.getLong("nroSerie"));

                long codigoEstacion = rs.getLong("codigoEstacion");
                EstacionSismologica estacion = estacionDAO.findById(codigoEstacion);
                s.setEstacionSismologica(estacion);

                long idModelo = rs.getLong("idModelo");
                ModeloSismografo modelo = modeloDAO.findById(idModelo);
                s.setModelo(modelo);

                long idSismografo = s.getIdentificadorSismografo();
                List<CambioEstado> cambios = cambioEstadoDAO.findBySismografoId(conn, idSismografo);
                s.setCambioEstado(cambios);

                List<Reparacion> reparaciones = reparacionDAO.findBySismografoId(conn, idSismografo);
                s.setReparacion(reparaciones);

                Estado estadoActual = findEstadoActual(conn, idSismografo);
                s.setEstadoActual(estadoActual);

                list.add(s);
            }
        }
        return list;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES
    // ==============================================================

    private void insertCambioEstado(Connection conn, long idSismografo, List<CambioEstado> cambios) throws SQLException {
        String sql = "INSERT INTO CambioEstado_Sismografo (idSismografo, idCambioEstado) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CambioEstado ce : cambios) {
                ps.setLong(1, idSismografo);
                ps.setLong(2, ce.getIdCambioEstado());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertReparacion(Connection conn, long idSismografo, List<Reparacion> reparaciones) throws SQLException {
        String sql = "INSERT INTO Reparacion_Sismografo (idSismografo, idReparacion) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Reparacion r : reparaciones) {
                ps.setLong(1, idSismografo);
                ps.setLong(2, r.getNroReparacion());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteCambioEstado(Connection conn, long idSismografo) throws SQLException {
        String sql = "DELETE FROM CambioEstado_Sismografo WHERE idSismografo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            ps.executeUpdate();
        }
    }

    private void deleteReparacion(Connection conn, long idSismografo) throws SQLException {
        String sql = "DELETE FROM Reparacion_Sismografo WHERE idSismografo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            ps.executeUpdate();
        }
    }

    private Estado findEstadoActual(Connection conn, long idSismografo) throws SQLException {
        String sql = """
            SELECT ce.* FROM CambioEstado ce
            JOIN CambioEstado_Sismografo ces ON ce.idCambioEstado = ces.idCambioEstado
            WHERE ces.idSismografo = ? AND ce.fechaHoraFin IS NULL
            ORDER BY ce.fechaHoraInicio DESC LIMIT 1
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long idCambioEstado = rs.getLong("idCambioEstado");
                    CambioEstado ce = cambioEstadoDAO.findById(idCambioEstado);
                    return ce != null ? ce.getEstado() : null;
                }
            }
        }
        return null;
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}