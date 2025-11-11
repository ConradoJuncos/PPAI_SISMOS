package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.SerieTemporal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class SerieTemporalDAO {

    // Dependencias
    private final EstadoDAO estadoActualDAO = new EstadoDAO();

    /* --------------------------------------------------------------
       INSERT – guarda datos principales + relación N:N con muestras
       -------------------------------------------------------------- */
    public void insert(SerieTemporal s) throws SQLException {
        String sql = """
            INSERT INTO SerieTemporal 
            (condicionAlarma, fechaHoraRegistro, frecuenciaMuestreo, nombreEstado, ambitoEstado) 
            VALUES (?, ?, ?, ?)
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

                    // Persistir relación N:N con muestras sísmicas
                    insertMuestras(conn, idSerie, s.getIdMuestraSismica());
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todo (incluyendo muestras)
       -------------------------------------------------------------- */
    public void update(SerieTemporal s) throws SQLException {
        String sql = """
            UPDATE SerieTemporal SET 
            condicionAlarma = ?, fechaHoraRegistro = ?, frecuenciaMuestreo = ?, nombreEstado = ?, ambitoEstado = ?, 
            WHERE idSerieTemporal = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getCondicionAlarma());
            ps.setObject(2, s.getFechaHoraRegistro());
            ps.setString(3, s.getFrecuenciaMuestreo());
            ps.setString(4, s.getEstado().getNombreEstado());
            ps.setString(5, s.getEstado().getAmbito());
            ps.setLong  (6, s.getIdSerieTemporal());

            ps.executeUpdate();

            // Actualizar relación N:N
            deleteMuestras(conn, s.getIdSerieTemporal());
            insertMuestras(conn, s.getIdSerieTemporal(), s.getIdMuestraSismica());
        }
    }

    /* --------------------------------------------------------------
       DELETE – elimina registro + relaciones
       -------------------------------------------------------------- */
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

    /* --------------------------------------------------------------
       FIND BY ID – carga todo (incluyendo muestras)
       -------------------------------------------------------------- */
    public SerieTemporal findById(long idSerieTemporal) throws SQLException {
        String sql = "SELECT * FROM SerieTemporal WHERE idSerieTemporal = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idSerieTemporal);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SerieTemporal s = new SerieTemporal();

                    s.setIdSerieTemporal(rs.getLong("idSerieTemporal"));
                    s.setCondicionAlarma(rs.getString("condicionAlarma"));
                    s.setFechaHoraRegistro(getLocalDateTime(rs, "fechaHoraRegistro"));
                    s.setFrecuenciaMuestreo(rs.getString("frecuenciaMuestreo"));
                    // Carga del Estado Actual (Directa)
                    String ambito = rs.getString("ambitoEstado");
                    String nombreEstado = rs.getString("nombreEstado");
                    s.setEstado(estadoActualDAO.findByAmbitoAndNombre(ambito, nombreEstado));

                    // Cargar muestras sísmicas
                    List<Long> muestras = findMuestrasBySerieTemporal(conn, idSerieTemporal);
                    s.setIdMuestraSismica(muestras);

                    return s;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<SerieTemporal> findAll() throws SQLException {
        String sql = "SELECT * FROM SerieTemporal";
        List<SerieTemporal> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SerieTemporal s = new SerieTemporal();

                s.setIdSerieTemporal(rs.getLong("idSerieTemporal"));
                s.setCondicionAlarma(rs.getString("condicionAlarma"));
                s.setFechaHoraRegistro(getLocalDateTime(rs, "fechaHoraRegistro"));
                s.setFrecuenciaMuestreo(rs.getString("frecuenciaMuestreo"));
                // Carga del Estado Actual (Directa)
                String ambito = rs.getString("ambitoEstado");
                String nombreEstado = rs.getString("nombreEstado");
                s.setEstado(estadoActualDAO.findByAmbitoAndNombre(ambito, nombreEstado));

                List<Long> muestras = findMuestrasBySerieTemporal(conn, s.getIdSerieTemporal());
                s.setIdMuestraSismica(muestras);

                list.add(s);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       NUEVO: Buscar Series Temporales por Evento Sísmico (para EventoSismicoDAO)
       -------------------------------------------------------------- */
    public List<SerieTemporal> findByEventoSismicoId(Connection conn, long idEventoSismico) throws SQLException {
        String sql = """
            SELECT st.* FROM SerieTemporal st
            JOIN EventoSismico_SerieTemporal est ON st.idSerieTemporal = est.idSerieTemporal
            WHERE est.idEventoSismico = ?
            """;
        List<SerieTemporal> series = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idEventoSismico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SerieTemporal s = new SerieTemporal();

                    s.setIdSerieTemporal(rs.getLong("idSerieTemporal"));
                    s.setCondicionAlarma(rs.getString("condicionAlarma"));
                    s.setFechaHoraRegistro(getLocalDateTime(rs, "fechaHoraRegistro"));
                    s.setFrecuenciaMuestreo(rs.getString("frecuenciaMuestreo"));
                    // Carga del Estado Actual (Directa)
                    String ambito = rs.getString("ambitoEstado");
                    String nombreEstado = rs.getString("nombreEstado");
                    s.setEstado(estadoActualDAO.findByAmbitoAndNombre(ambito, nombreEstado));

                    List<Long> muestras = findMuestrasBySerieTemporal(conn, s.getIdSerieTemporal());
                    s.setIdMuestraSismica(muestras);

                    series.add(s);
                }
            }
        }
        return series;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES PARA RELACIÓN N:N CON MUESTRAS SÍSMICAS
    // ==============================================================

    private void insertMuestras(Connection conn, long idSerie, List<Long> muestras) throws SQLException {
        String sql = "INSERT INTO SerieTemporal_MuestraSismica (idSerieTemporal, idMuestraSismica) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Long idMuestra : muestras) {
                ps.setLong(1, idSerie);
                ps.setLong(2, idMuestra);
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

    private List<Long> findMuestrasBySerieTemporal(Connection conn, long idSerie) throws SQLException {
        String sql = "SELECT idMuestraSismica FROM SerieTemporal_MuestraSismica WHERE idSerieTemporal = ?";
        List<Long> muestras = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSerie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    muestras.add(rs.getLong("idMuestraSismica"));
                }
            }
        }
        return muestras;
    }

    // ==============================================================
    // UTILIDAD: convertir Timestamp a LocalDateTime
    // ==============================================================
    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}