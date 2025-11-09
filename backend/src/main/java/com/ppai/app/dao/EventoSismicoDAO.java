package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class EventoSismicoDAO {

    private final ClasificacionSismoDAO clasificacionDAO = new ClasificacionSismoDAO();
    private final MagnitudRichterDAO magnitudDAO = new MagnitudRichterDAO();
    private final OrigenDeGeneracionDAO origenDAO = new OrigenDeGeneracionDAO();
    private final AlcanceSismoDAO alcanceDAO = new AlcanceSismoDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final CambioEstadoDAO cambioEstadoDAO = new CambioEstadoDAO();
    private final EstadoDAO estadoActualDAO = new EstadoDAO(); // <-- Usaremos este para cargar el estado

    /* --------------------------------------------------------------
       INSERT – AHORA incluye FK compuesta a Estado (2 parámetros nuevos)
       -------------------------------------------------------------- */
    public void insert(EventoSismico e) throws SQLException {
        String sql = """
            INSERT INTO EventoSismico (
                fechaHoraOcurrencia, fechaHoraFin, latitudEpicentro, latitudHipocentro,
                longitudEpicentro, longitudHipocentro, valorMagnitud,
                idClasificacionSismo, idMagnitudRichter, idOrigenGeneracion,
                idAlcanceSismo, idAnalistaSupervisor, ambitoEstadoActual, nombreEstadoActual
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) -- 14 placeholders
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, e.getFechaHoraOcurrencia());
            ps.setObject(2, e.getFechaHoraFin());
            ps.setString(3, e.getLatitudEpicentro());
            ps.setString(4, e.getLatitudHipocentro());
            ps.setString(5, e.getLongitudEpicentro());
            ps.setString(6, e.getLongitudHipocentro());
            ps.setDouble(7, e.getValorMagnitud());
            ps.setLong  (8, e.getClasificacionSismo().getIdClasificacionSismo());
            ps.setInt   (9, e.getMagnitudRichter().getNumero());
            ps.setLong  (10, e.getOrigenGegeneracion().getOrigenDeGeneracion());
            ps.setLong  (11, e.getAlcanceSismo().getIdAlcanceSismo());
            ps.setObject(12, e.getAnalistaSupervisor() != null ? e.getAnalistaSupervisor().getIdEmpleado() : null);
            
            // CAMPOS NUEVOS: FK al Estado Actual
            ps.setString(13, e.getEstadoActual().getAmbito());
            ps.setString(14, e.getEstadoActual().getNombreEstado());

            ps.executeUpdate();

            // ... (Resto de la lógica de INSERT: obtener keys, insertar SerieTemporal, CambioEstado) ...
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idEvento = rs.getLong(1);
                    e.setIdEventoSismico(idEvento);

                    // Persistir relaciones 1:N
                    insertSerieTemporal(conn, idEvento, e.getSerieTemporal());
                    insertCambioEstado(conn, idEvento, e.getCambioEstado());
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – AHORA incluye FK compuesta a Estado (2 parámetros nuevos)
       -------------------------------------------------------------- */
    public void update(EventoSismico e) throws SQLException {
        String sql = """
            UPDATE EventoSismico SET
                fechaHoraOcurrencia = ?, fechaHoraFin = ?, latitudEpicentro = ?, latitudHipocentro = ?,
                longitudEpicentro = ?, longitudHipocentro = ?, valorMagnitud = ?,
                idClasificacionSismo = ?, idMagnitudRichter = ?, idOrigenGeneracion = ?,
                idAlcanceSismo = ?, idAnalistaSupervisor = ?,
                ambitoEstadoActual = ?, nombreEstadoActual = ? -- CAMPOS NUEVOS
            WHERE idEventoSismico = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, e.getFechaHoraOcurrencia());
            ps.setObject(2, e.getFechaHoraFin());
            ps.setString(3, e.getLatitudEpicentro());
            ps.setString(4, e.getLatitudHipocentro());
            ps.setString(5, e.getLongitudEpicentro());
            ps.setString(6, e.getLongitudHipocentro());
            ps.setDouble(7, e.getValorMagnitud());
            ps.setLong  (8, e.getClasificacionSismo().getIdClasificacionSismo());
            ps.setInt   (9, e.getMagnitudRichter().getNumero());
            ps.setLong  (10, e.getOrigenGegeneracion().getOrigenDeGeneracion());
            ps.setLong  (11, e.getAlcanceSismo().getIdAlcanceSismo());
            ps.setObject(12, e.getAnalistaSupervisor() != null ? e.getAnalistaSupervisor().getIdEmpleado() : null);

            // CAMPOS NUEVOS: FK al Estado Actual
            ps.setString(13, e.getEstadoActual().getAmbito());
            ps.setString(14, e.getEstadoActual().getNombreEstado());

            ps.setLong  (15, e.getIdEventoSismico()); // Cláusula WHERE

            ps.executeUpdate();

            // ... (Resto de la lógica de UPDATE: manejar SerieTemporal y CambioEstado) ...
            deleteSerieTemporal(conn, e.getIdEventoSismico());
            deleteCambioEstado(conn, e.getIdEventoSismico());
            insertSerieTemporal(conn, e.getIdEventoSismico(), e.getSerieTemporal());
            insertCambioEstado(conn, e.getIdEventoSismico(), e.getCambioEstado());
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga el Estado Actual directamente
       -------------------------------------------------------------- */
    public EventoSismico findById(long idEventoSismico) throws SQLException {
        // La consulta SELECT trae los dos campos de la FK compuesta
        String sql = "SELECT * FROM EventoSismico WHERE idEventoSismico = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idEventoSismico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EventoSismico e = new EventoSismico();

                    e.setIdEventoSismico(rs.getLong("idEventoSismico"));
                    // ... (resto de la carga de atributos simples y relaciones 1:1) ...
                    e.setFechaHoraOcurrencia(getLocalDateTime(rs, "fechaHoraOcurrencia"));
                    e.setFechaHoraFin(getLocalDateTime(rs, "fechaHoraFin"));
                    e.setLatitudEpicentro(rs.getString("latitudEpicentro"));
                    e.setLatitudHipocentro(rs.getString("latitudHipocentro"));
                    e.setLongitudEpicentro(rs.getString("longitudEpicentro"));
                    e.setLongitudHipocentro(rs.getString("longitudHipocentro"));
                    e.setValorMagnitud(rs.getDouble("valorMagnitud"));

                    // Objetos relacionados
                    e.setClasificacionSismo(clasificacionDAO.findById(rs.getLong("idClasificacionSismo")));
                    e.setMagnitudRichter(magnitudDAO.findByNumero(rs.getInt("idMagnitudRichter")));
                    e.setOrigenDeGeneracion(origenDAO.findById(rs.getLong("idOrigenGeneracion")));
                    e.setAlcanceSismo(alcanceDAO.findById(rs.getLong("idAlcanceSismo")));
                    Long idAnalista = rs.getObject("idAnalistaSupervisor", Long.class);
                    e.setAnalistaSupervisor(idAnalista != null ? empleadoDAO.findById(idAnalista) : null);


                    // Carga del Estado Actual (Directa)
                    String ambito = rs.getString("ambitoEstadoActual");
                    String nombreEstado = rs.getString("nombreEstadoActual");
                    e.setEstadoActual(estadoActualDAO.findByAmbitoAndNombre(ambito, nombreEstado));


                    // Listas 1:N
                    e.setSerieTemporal(findSerieTemporalByEvento(conn, idEventoSismico));
                    e.setCambioEstado(cambioEstadoDAO.findByEventoSismicoId(conn, idEventoSismico));

                    // El antiguo "Estado actual" por historial ya no se usa, la propiedad ya se cargó arriba.
                    // e.setEstadoActual(findEstadoActual(conn, idEventoSismico)); // <-- ESTE CÓDIGO SE VUELVE OBSOLETO

                    return e;
                }
            }
        }
        return null;
    }

    // ... (El resto de los métodos como delete, findAll, y auxiliares 1:N permanecen iguales) ...

    /* --------------------------------------------------------------
       MÉTODOS AUXILIARES
       -------------------------------------------------------------- */

    // NOTA IMPORTANTE: La lógica de findEstadoActual ya NO es necesaria
    // porque el estado se carga directamente desde la tabla EventoSismico.
    // Si la dejamos, solo sirve como validación o fallback, pero la fuente
    // principal de verdad ahora es la columna FK de EventoSismico.
    private Estado findEstadoActual(Connection conn, long idEvento) throws SQLException {
        // ... (Este método es OBSOLETO. Se mantiene solo si el Negocio lo usa de forma interna) ...
        return null; // Retorna null o se elimina.
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
    
    // ... (insertSerieTemporal, insertCambioEstado, deleteSerieTemporal, deleteCambioEstado, findSerieTemporalByEvento) ...
    private void insertSerieTemporal(Connection conn, long idEvento, List<SerieTemporal> series) throws SQLException {
        String sql = "INSERT INTO EventoSismico_SerieTemporal (idEventoSismico, idSerieTemporal) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (SerieTemporal s : series) {
                ps.setLong(1, idEvento);
                ps.setLong(2, s.getIdSerieTemporal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertCambioEstado(Connection conn, long idEvento, List<CambioEstado> cambios) throws SQLException {
        String sql = "INSERT INTO EventoSismico_CambioEstado (idEventoSismico, idCambioEstado) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CambioEstado ce : cambios) {
                ps.setLong(1, idEvento);
                ps.setLong(2, ce.getIdCambioEstado());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteSerieTemporal(Connection conn, long idEvento) throws SQLException {
        String sql = "DELETE FROM EventoSismico_SerieTemporal WHERE idEventoSismico = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idEvento);
            ps.executeUpdate();
        }
    }

    private void deleteCambioEstado(Connection conn, long idEvento) throws SQLException {
        String sql = "DELETE FROM EventoSismico_CambioEstado WHERE idEventoSismico = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idEvento);
            ps.executeUpdate();
        }
    }

    private List<SerieTemporal> findSerieTemporalByEvento(Connection conn, long idEvento) throws SQLException {
        // Implementar con SerieTemporalDAO cuando exista
        return new ArrayList<>();
    }
}