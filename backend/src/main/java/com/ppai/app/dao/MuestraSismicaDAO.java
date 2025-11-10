package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.MuestraSismica;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MuestraSismicaDAO {

    /* --------------------------------------------------------------
       INSERT – guarda datos principales + relación N:N con detalles
       -------------------------------------------------------------- */
    public void insert(MuestraSismica m) throws SQLException {
        String sql = "INSERT INTO MuestraSismica (fechaHoraMuestraSismica) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, m.getFechaHoraMuestraSismica());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idMuestra = rs.getLong(1);
                    m.setIdMuestraSismica(idMuestra);

                    // Persistir relación N:N con detalles
                    insertDetalles(conn, idMuestra, m.getIdDetalleMuestraSismica());
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todo (incluyendo detalles)
       -------------------------------------------------------------- */
    public void update(MuestraSismica m) throws SQLException {
        String sql = "UPDATE MuestraSismica SET fechaHoraMuestraSismica = ? WHERE idMuestraSismica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, m.getFechaHoraMuestraSismica());
            ps.setLong  (2, m.getIdMuestraSismica());

            ps.executeUpdate();

            // Actualizar relación N:N
            deleteDetalles(conn, m.getIdMuestraSismica());
            insertDetalles(conn, m.getIdMuestraSismica(), m.getIdDetalleMuestraSismica());
        }
    }

    /* --------------------------------------------------------------
       DELETE – elimina registro + relaciones
       -------------------------------------------------------------- */
    public void delete(long idMuestraSismica) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            deleteDetalles(conn, idMuestraSismica);

            String sql = "DELETE FROM MuestraSismica WHERE idMuestraSismica = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idMuestraSismica);
                ps.executeUpdate();
            }
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga todo (incluyendo detalles)
       -------------------------------------------------------------- */
    public MuestraSismica findById(long idMuestraSismica) throws SQLException {
        String sql = "SELECT * FROM MuestraSismica WHERE idMuestraSismica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idMuestraSismica);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MuestraSismica m = new MuestraSismica();

                    m.setIdMuestraSismica(rs.getLong("idMuestraSismica"));
                    m.setFechaHoraMuestraSismica(getLocalDateTime(rs, "fechaHoraMuestraSismica"));

                    // Cargar detalles
                    List<Long> detalles = findDetallesByMuestra(conn, idMuestraSismica);
                    m.setIdDetalleMuestraSismica(detalles);

                    return m;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<MuestraSismica> findAll() throws SQLException {
        String sql = "SELECT * FROM MuestraSismica";
        List<MuestraSismica> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MuestraSismica m = new MuestraSismica();

                m.setIdMuestraSismica(rs.getLong("idMuestraSismica"));
                m.setFechaHoraMuestraSismica(getLocalDateTime(rs, "fechaHoraMuestraSismica"));

                List<Long> detalles = findDetallesByMuestra(conn, m.getIdMuestraSismica());
                m.setIdDetalleMuestraSismica(detalles);

                list.add(m);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       NUEVO: Buscar Muestras por Serie Temporal (para SerieTemporalDAO)
       -------------------------------------------------------------- */
    public List<MuestraSismica> findBySerieTemporalId(Connection conn, long idSerieTemporal) throws SQLException {
        String sql = """
            SELECT ms.* FROM MuestraSismica ms
            JOIN SerieTemporal_MuestraSismica stms ON ms.idMuestraSismica = stms.idMuestraSismica
            WHERE stms.idSerieTemporal = ?
            """;
        List<MuestraSismica> muestras = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSerieTemporal);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MuestraSismica m = new MuestraSismica();

                    m.setIdMuestraSismica(rs.getLong("idMuestraSismica"));
                    m.setFechaHoraMuestraSismica(getLocalDateTime(rs, "fechaHoraMuestraSismica"));

                    List<Long> detalles = findDetallesByMuestra(conn, m.getIdMuestraSismica());
                    m.setIdDetalleMuestraSismica(detalles);

                    muestras.add(m);
                }
            }
        }
        return muestras;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES PARA RELACIÓN N:N CON DETALLES
    // ==============================================================

    private void insertDetalles(Connection conn, long idMuestra, List<Long> detalles) throws SQLException {
        String sql = "INSERT INTO MuestraSismica_DetalleMuestraSismica (idMuestraSismica, idDetalleMuestraSismica) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Long idDetalle : detalles) {
                ps.setLong(1, idMuestra);
                ps.setLong(2, idDetalle);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteDetalles(Connection conn, long idMuestra) throws SQLException {
        String sql = "DELETE FROM MuestraSismica_DetalleMuestraSismica WHERE idMuestraSismica = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMuestra);
            ps.executeUpdate();
        }
    }

    private List<Long> findDetallesByMuestra(Connection conn, long idMuestra) throws SQLException {
        String sql = "SELECT idDetalleMuestraSismica FROM MuestraSismica_DetalleMuestraSismica WHERE idMuestraSismica = ?";
        List<Long> detalles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMuestra);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(rs.getLong("idDetalleMuestraSismica"));
                }
            }
        }
        return detalles;
    }

    // ==============================================================
    // UTILIDAD: convertir Timestamp a LocalDateTime
    // ==============================================================
    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}