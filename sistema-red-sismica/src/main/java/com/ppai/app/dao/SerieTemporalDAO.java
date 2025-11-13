package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class SerieTemporalDAO {

    // Dependencias
    private final EstadoDAO estadoDAO = new EstadoDAO();
    private final MuestraSismicaDAO muestraSismicaDAO = new MuestraSismicaDAO();

    /*
     * --------------------------------------------------------------
     * INSERT – guarda datos principales + relación N:N con muestras
     * --------------------------------------------------------------
     */
    public void insert(SerieTemporal s) throws SQLException {
        String sql = """
                INSERT INTO SerieTemporal
                (condicionAlarma, fechaHoraRegistro, frecuenciaMuestreo, nombreEstado, ambitoEstado)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getCondicionAlarma());
            ps.setObject(2, s.getFechaHoraRegistro());
            ps.setString(3, s.getFrecuenciaMuestreo());
            ps.setString(4, s.getEstado().getNombreEstado());
            ps.setString(5, s.getEstado().getAmbito());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idSerie = rs.getLong(1);
                    s.setIdSerieTemporal(idSerie);

                    // Guardar relación con muestras sísmicas
                    insertMuestras(conn, idSerie, s.getMuestrasSismicas());
                }
            }
        }
    }

    /*
     * --------------------------------------------------------------
     * UPDATE – actualiza todo (incluyendo muestras)
     * --------------------------------------------------------------
     */
    public void update(SerieTemporal s) throws SQLException {
        String sql = """
                UPDATE SerieTemporal SET
                    condicionAlarma = ?,
                    fechaHoraRegistro = ?,
                    frecuenciaMuestreo = ?,
                    nombreEstado = ?,
                    ambitoEstado = ?
                WHERE idSerieTemporal = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getCondicionAlarma());
            ps.setObject(2, s.getFechaHoraRegistro());
            ps.setString(3, s.getFrecuenciaMuestreo());
            ps.setString(4, s.getEstado().getNombreEstado());
            ps.setString(5, s.getEstado().getAmbito());
            ps.setLong(6, s.getIdSerieTemporal());

            ps.executeUpdate();

            // Actualizar relación N:N
            deleteMuestras(conn, s.getIdSerieTemporal());
            insertMuestras(conn, s.getIdSerieTemporal(), s.getMuestrasSismicas());
        }
    }

    /*
     * --------------------------------------------------------------
     * DELETE – elimina registro + relaciones
     * --------------------------------------------------------------
     */
    public void delete(long idSerieTemporal) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            deleteMuestras(conn, idSerieTemporal);

            String sql = "DELETE FROM SerieTemporal WHERE idSerieTemporal = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idSerieTemporal);
                ps.executeUpdate();
            }
        }
    }

    /*
     * Busca una serie temporal por su ID.
     * Carga automáticamente todas sus muestras sísmicas asociadas.
     */
    public SerieTemporal findById(long idSerieTemporal) throws SQLException {
        String sql = "SELECT * FROM SerieTemporal WHERE idSerieTemporal = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idSerieTemporal);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSerieTemporal(rs, conn, idSerieTemporal);
                }
            }
        }
        return null;
    }

    /*
     * --------------------------------------------------------------
     * FIND ALL – lista completa
     * --------------------------------------------------------------
     */
    public List<SerieTemporal> findAll() throws SQLException {
        String sql = "SELECT idSerieTemporal FROM SerieTemporal";
        List<SerieTemporal> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("idSerieTemporal");
                SerieTemporal s = findById(id);
                if (s != null)
                    list.add(s);
            }
        }
        return list;
    }

    /*
     * Busca todas las series temporales asociadas a un evento sísmico.
     * Utiliza la conexión proporcionada para mantener coherencia transaccional
     * y garantizar que las muestras sísmicas se carguen correctamente.
     */
    public List<SerieTemporal> findByEventoSismicoId(Connection conn, long idEventoSismico) throws SQLException {
        String sql = """
                SELECT st.idSerieTemporal
                FROM SerieTemporal st
                JOIN EventoSismico_SerieTemporal est
                  ON st.idSerieTemporal = est.idSerieTemporal
                WHERE est.idEventoSismico = ?
                """;

        List<SerieTemporal> series = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idEventoSismico);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long idSerie = rs.getLong("idSerieTemporal");
                    // Usar la conexión existente para evitar múltiples conexiones
                    SerieTemporal s = findByIdWithConnection(conn, idSerie);
                    if (s != null)
                        series.add(s);
                }
            }
        }
        return series;
    }

    /*
     * Carga una serie temporal completa usando una conexión existente.
     * Este método es utilizado internamente para evitar abrir múltiples conexiones
     * y mantener la coherencia transaccional.
     */
    private SerieTemporal findByIdWithConnection(Connection conn, long idSerieTemporal) throws SQLException {
        String sql = "SELECT * FROM SerieTemporal WHERE idSerieTemporal = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSerieTemporal);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSerieTemporal(rs, conn, idSerieTemporal);
                }
            }
        }
        return null;
    }

    /*
     * Mapea un ResultSet a un objeto SerieTemporal y carga sus muestras asociadas.
     * Este método centraliza la lógica de construcción del objeto para mayor cohesión.
     * Delega la carga de muestras al DAO especializado que reutiliza la conexión.
     */
    private SerieTemporal mapResultSetToSerieTemporal(ResultSet rs, Connection conn, long idSerieTemporal) throws SQLException {
        SerieTemporal s = new SerieTemporal();

        s.setIdSerieTemporal(rs.getLong("idSerieTemporal"));
        s.setCondicionAlarma(rs.getString("condicionAlarma"));
        s.setFechaHoraRegistro(getLocalDateTime(rs, "fechaHoraRegistro"));
        s.setFrecuenciaMuestreo(rs.getString("frecuenciaMuestreo"));

        // Cargar estado asociado
        String ambito = rs.getString("ambitoEstado");
        String nombreEstado = rs.getString("nombreEstado");
        Estado estado = estadoDAO.findByAmbitoAndNombre(ambito, nombreEstado);
        s.setEstado(estado);

        // Cargar muestras sísmicas usando el DAO especializado con la conexión existente
        // Esto garantiza que cada muestra cargará sus detalles correctamente sin abrir nuevas conexiones
        List<MuestraSismica> muestras = muestraSismicaDAO.findBySerieTemporalId(conn, idSerieTemporal);
        s.setMuestrasSismicas(muestras);

        return s;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES PARA RELACIÓN N:N CON MUESTRAS SÍSMICAS
    // ==============================================================

    private void insertMuestras(Connection conn, long idSerie, List<MuestraSismica> muestras) throws SQLException {
        String sql = """
                INSERT INTO SerieTemporal_MuestraSismica (idSerieTemporal, idMuestraSismica)
                VALUES (?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (MuestraSismica m : muestras) {
                ps.setLong(1, idSerie);
                ps.setLong(2, m.getIdMuestraSismica());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteMuestras(Connection conn, long idSerie) throws SQLException {
        String sql = "DELETE FROM SerieTemporal_MuestraSismica WHERE idSerieTemporal = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSerie);
            ps.executeUpdate();
        }
    }

    // ==============================================================
    // UTILIDAD: convertir Timestamp a LocalDateTime
    // ==============================================================
    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }

    public Map<Integer, Integer> getRelacionesEventoSerie() throws SQLException {
        Map<Integer, Integer> relaciones = new HashMap<>();
        String sql = "SELECT idSerieTemporal, idEventoSismico FROM SerieTemporal";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                relaciones.put(rs.getInt("idSerieTemporal"), rs.getInt("idEventoSismico"));
            }
        }
        return relaciones;
    }

}
