package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class SismografoDAO {

    // Dependencias
    private final EstacionSismologicaDAO estacionDAO = new EstacionSismologicaDAO();
    private final ModeloSismografoDAO modeloDAO = new ModeloSismografoDAO();
    private final CambioEstadoDAO cambioEstadoDAO = new CambioEstadoDAO();
    private final ReparacionDAO reparacionDAO = new ReparacionDAO(); // ← Asumimos que existe

    /* --------------------------------------------------------------
       INSERT – guarda datos principales + relaciones 1:N
       -------------------------------------------------------------- */
    public void insert(Sismografo s) throws SQLException {
        // La tabla Sismografo contiene las FKs a EstacionSismologica y ModeloSismografo
        String sql = "INSERT INTO Sismografo " +
                     "(fechaAdquisicion, nroSerie, codigoEstacion, idModelo) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, s.getFechaAdquisicion());
            ps.setLong  (2, s.getNroSerie());
            // FK a EstacionSismologica
            ps.setLong  (3, s.getEstacionSismologica().getCodigoEstacion());
            // FK a ModeloSismografo
            ps.setLong  (4, s.getModelo().getIdModeloSismografo());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idSismografo = rs.getLong(1);
                    s.setIdentificadorSismografo(idSismografo);

                    // Persistir relaciones 1:N a través de tablas intermedias
                    if (s.getCambioEstado() != null && !s.getCambioEstado().isEmpty()) {
                        insertCambioEstado(conn, idSismografo, s.getCambioEstado());
                    }
                    if (s.getReparacion() != null && !s.getReparacion().isEmpty()) {
                        insertReparacion(conn, idSismografo, s.getReparacion());
                    }
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

            // Actualizar relaciones 1:N (Estrategia de Delete-then-Insert)
            deleteCambioEstado(conn, s.getIdentificadorSismografo());
            deleteReparacion(conn, s.getIdentificadorSismografo());
            
            if (s.getCambioEstado() != null && !s.getCambioEstado().isEmpty()) {
                insertCambioEstado(conn, s.getIdentificadorSismografo(), s.getCambioEstado());
            }
            if (s.getReparacion() != null && !s.getReparacion().isEmpty()) {
                insertReparacion(conn, s.getIdentificadorSismografo(), s.getReparacion());
            }
        }
    }

    /* --------------------------------------------------------------
       DELETE – elimina registro + relaciones
       -------------------------------------------------------------- */
    public void delete(long identificadorSismografo) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Eliminar referencias en las tablas intermedias
            deleteCambioEstado(conn, identificadorSismografo);
            deleteReparacion(conn, identificadorSismografo);

            // 2. Eliminar el registro principal
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
                    return mapResultSetToSismografo(rs, conn);
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
                list.add(mapResultSetToSismografo(rs, conn));
            }
        }
        return list;
    }
    
    // ==============================================================
    // MÉTODOS AUXILIARES Y MAPEADORES
    // ==============================================================

    /**
     * Mapea un ResultSet a la entidad Sismografo, delegando la carga de
     * objetos y listas relacionados.
     */
    private Sismografo mapResultSetToSismografo(ResultSet rs, Connection conn) throws SQLException {
        Sismografo s = new Sismografo();

        long identificadorSismografo = rs.getLong("identificadorSismografo");
        s.setIdentificadorSismografo(identificadorSismografo);
        s.setFechaAdquisicion(getLocalDateTime(rs, "fechaAdquisicion"));
        s.setNroSerie(rs.getLong("nroSerie"));

        // 1. Cargar objetos relacionados (1:1 o M:1)
        long codigoEstacion = rs.getLong("codigoEstacion");
        EstacionSismologica estacion = estacionDAO.findById(codigoEstacion);
        s.setEstacionSismologica(estacion);

        long idModelo = rs.getLong("idModelo");
        ModeloSismografo modelo = modeloDAO.findById(idModelo);
        s.setModelo(modelo);

        // 2. Cargar listas 1:N (Delegación a DAOs correspondientes)
        // Se asume que estos métodos buscan en las tablas intermedias
        List<CambioEstado> cambios = cambioEstadoDAO.findBySismografoId(conn, identificadorSismografo);
        s.setCambioEstado(cambios);

        List<Reparacion> reparaciones = reparacionDAO.findBySismografoId(conn, identificadorSismografo);
        s.setReparacion(reparaciones);

        // 3. Atributo derivado: Estado actual
        Estado estadoActual = findEstadoActual(conn, identificadorSismografo);
        s.setEstadoActual(estadoActual);

        return s;
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }

    /* --------------------------------------------------------------
       LÓGICA DE ESTADO ACTUAL (Atributo derivado)
       -------------------------------------------------------------- */
    private Estado findEstadoActual(Connection conn, long idSismografo) throws SQLException {
        // Busca el último CambioEstado asociado al sismógrafo que no tiene fechaHoraFin (es decir, el actual).
        String sql = """
            SELECT ce.idCambioEstado FROM CambioEstado ce
            JOIN CambioEstado_Sismografo ces ON ce.idCambioEstado = ces.idCambioEstado
            WHERE ces.idSismografo = ? AND ce.fechaHoraFin IS NULL
            ORDER BY ce.fechaHoraInicio DESC LIMIT 1
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long idCambioEstado = rs.getLong("idCambioEstado");
                    // Delega la carga completa del CambioEstado al DAO
                    CambioEstado ce = cambioEstadoDAO.findById(idCambioEstado);
                    return ce != null ? ce.getEstado() : null;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       LÓGICA DE RELACIÓN 1:N (a través de tablas intermedias)
       -------------------------------------------------------------- */
    private void insertCambioEstado(Connection conn, long idSismografo, List<CambioEstado> cambios) throws SQLException {
        // Enlaza el Sismografo con sus CambioEstado en la tabla intermedia
        String sql = "INSERT INTO CambioEstado_Sismografo (idSismografo, idCambioEstado) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CambioEstado ce : cambios) {
                // Se asume que el ce ya fue insertado/tiene ID asignado
                ps.setLong(1, idSismografo);
                ps.setLong(2, ce.getIdCambioEstado());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteCambioEstado(Connection conn, long idSismografo) throws SQLException {
        // Elimina el enlace entre Sismografo y CambioEstado
        String sql = "DELETE FROM CambioEstado_Sismografo WHERE idSismografo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            ps.executeUpdate();
        }
    }

    private void insertReparacion(Connection conn, long idSismografo, List<Reparacion> reparaciones) throws SQLException {
        // Enlaza el Sismografo con sus Reparacion en la tabla intermedia
        String sql = "INSERT INTO Reparacion_Sismografo (idSismografo, idReparacion) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Reparacion r : reparaciones) {
                // Se asume que la r ya fue insertada/tiene ID asignado
                ps.setLong(1, idSismografo);
                ps.setLong(2, r.getNroReparacion());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteReparacion(Connection conn, long idSismografo) throws SQLException {
        // Elimina el enlace entre Sismografo y Reparacion
        String sql = "DELETE FROM Reparacion_Sismografo WHERE idSismografo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSismografo);
            ps.executeUpdate();
        }
    }
}