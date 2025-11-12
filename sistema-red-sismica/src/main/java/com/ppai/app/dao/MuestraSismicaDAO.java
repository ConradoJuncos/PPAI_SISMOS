package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MuestraSismicaDAO {

    // Dependencia para cargar los objetos detalle
    private final DetalleMuestraSismicaDAO detalleDAO = new DetalleMuestraSismicaDAO();

    /*
     * --------------------------------------------------------------
     * INSERT – guarda datos principales + relación N:N con detalles
     * --------------------------------------------------------------
     */
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
                    insertDetalles(conn, idMuestra, m.getDetalleMuestrasSismicas());
                }
            }
        }
    }

    /*
     * --------------------------------------------------------------
     * UPDATE – actualiza todo (incluyendo detalles)
     * --------------------------------------------------------------
     */
    public void update(MuestraSismica m) throws SQLException {
        String sql = "UPDATE MuestraSismica SET fechaHoraMuestraSismica = ? WHERE idMuestraSismica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, m.getFechaHoraMuestraSismica());
            ps.setLong(2, m.getIdMuestraSismica());
            ps.executeUpdate();

            // Actualizar relación N:N
            deleteDetalles(conn, m.getIdMuestraSismica());
            insertDetalles(conn, m.getIdMuestraSismica(), m.getDetalleMuestrasSismicas());
        }
    }

    /*
     * --------------------------------------------------------------
     * DELETE – elimina registro + relaciones
     * --------------------------------------------------------------
     */
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

    /*
     * Busca una muestra sísmica por su ID.
     * Carga automáticamente todos sus detalles sísmicos asociados.
     */
    public MuestraSismica findById(long idMuestraSismica) throws SQLException {
        String sql = "SELECT * FROM MuestraSismica WHERE idMuestraSismica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idMuestraSismica);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMuestraSismica(rs, conn, idMuestraSismica);
                }
            }
        }
        return null;
    }

    /*
     * Carga una muestra sísmica completa usando una conexión existente.
     * Este método es utilizado internamente para evitar abrir múltiples conexiones
     * y mantener la coherencia transaccional.
     */
    private MuestraSismica findByIdWithConnection(Connection conn, long idMuestraSismica) throws SQLException {
        String sql = "SELECT * FROM MuestraSismica WHERE idMuestraSismica = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMuestraSismica);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMuestraSismica(rs, conn, idMuestraSismica);
                }
            }
        }
        return null;
    }

    /*
     * Mapea un ResultSet a un objeto MuestraSismica y carga sus detalles asociados.
     * Este método centraliza la lógica de construcción del objeto para mayor cohesión.
     */
    private MuestraSismica mapResultSetToMuestraSismica(ResultSet rs, Connection conn, long idMuestraSismica)
            throws SQLException {
        MuestraSismica m = new MuestraSismica();

        m.setIdMuestraSismica(rs.getLong("idMuestraSismica"));
        m.setFechaHoraMuestraSismica(getLocalDateTime(rs, "fechaHoraMuestraSismica"));

        // Cargar detalles sísmicos usando la conexión existente
        List<DetalleMuestraSismica> detalles = findDetallesByMuestra(conn, idMuestraSismica);
        m.setDetalleMuestrasSismicas(detalles);

        return m;
    }

    /*
     * --------------------------------------------------------------
     * FIND ALL – lista completa
     * --------------------------------------------------------------
     */
    public List<MuestraSismica> findAll() throws SQLException {
        String sql = "SELECT idMuestraSismica FROM MuestraSismica";
        List<MuestraSismica> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MuestraSismica m = findById(rs.getLong("idMuestraSismica"));
                if (m != null)
                    list.add(m);
            }
        }
        return list;
    }

    /**
     * Busca todas las muestras sísmicas asociadas a una serie temporal.
     * Utiliza la conexión proporcionada para mantener coherencia transaccional
     * y garantizar que los detalles se carguen correctamente.
     *
     * @param conn Conexión existente a reutilizar
     * @param idSerieTemporal ID de la serie temporal
     * @return Lista de muestras sísmicas asociadas
     * @throws SQLException Si ocurre un error en la consulta
     */
    public List<MuestraSismica> findBySerieTemporalId(Connection conn, long idSerieTemporal) throws SQLException {
        String sql = """
                SELECT ms.idMuestraSismica
                FROM MuestraSismica ms
                JOIN SerieTemporal_MuestraSismica stms ON ms.idMuestraSismica = stms.idMuestraSismica
                WHERE stms.idSerieTemporal = ?
                """;
        List<MuestraSismica> muestras = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idSerieTemporal);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long idMuestra = rs.getLong("idMuestraSismica");
                    // Usar la conexión existente para evitar múltiples conexiones
                    MuestraSismica m = findByIdWithConnection(conn, idMuestra);
                    if (m != null)
                        muestras.add(m);
                }
            }
        }
        return muestras;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES PARA RELACIÓN N:N CON DETALLES
    // ==============================================================

    private void insertDetalles(Connection conn, long idMuestra, List<DetalleMuestraSismica> detalles)
            throws SQLException {
        String sql = "INSERT INTO MuestraSismica_DetalleMuestraSismica (idMuestraSismica, idDetalleMuestraSismica) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DetalleMuestraSismica d : detalles) {
                ps.setLong(1, idMuestra);
                ps.setLong(2, d.getIdDetalleMuestraSismica());
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

    private List<DetalleMuestraSismica> findDetallesByMuestra(Connection conn, long idMuestra) throws SQLException {
        String sql = "SELECT idDetalleMuestraSismica FROM MuestraSismica_DetalleMuestraSismica WHERE idMuestraSismica = ?";
        List<DetalleMuestraSismica> detalles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMuestra);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long idDetalle = rs.getLong("idDetalleMuestraSismica");
                    DetalleMuestraSismica d = detalleDAO.findById(idDetalle);
                    if (d != null)
                        detalles.add(d);
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

}
