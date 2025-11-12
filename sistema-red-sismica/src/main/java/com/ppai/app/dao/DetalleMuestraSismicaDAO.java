package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.util.*;

public class DetalleMuestraSismicaDAO {

    // Dependencia para cargar el TipoDeDato asociado
    private final TipoDeDatoDAO tipoDeDatoDAO = new TipoDeDatoDAO();

    /*
     * --------------------------------------------------------------
     * INSERT – guarda todos los campos con referencia al TipoDeDato
     * --------------------------------------------------------------
     */
    public void insert(DetalleMuestraSismica d) throws SQLException {
        String sql = "INSERT INTO DetalleMuestraSismica (idTipoDeDato, valor) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Se obtiene el id del tipo de dato referenciado
            ps.setLong(1, d.getTipoDeDato().getIdTipoDeDato());
            ps.setDouble(2, d.getValor());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    d.setIdDetalleMuestraSismica(rs.getLong(1));
                }
            }
        }
    }

    /*
     * --------------------------------------------------------------
     * UPDATE – actualiza todo
     * --------------------------------------------------------------
     */
    public void update(DetalleMuestraSismica d) throws SQLException {
        String sql = """
                UPDATE DetalleMuestraSismica
                SET idTipoDeDato = ?, valor = ?
                WHERE idDetalleMuestraSismica = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, d.getTipoDeDato().getIdTipoDeDato());
            ps.setDouble(2, d.getValor());
            ps.setLong(3, d.getIdDetalleMuestraSismica());
            ps.executeUpdate();
        }
    }

    /*
     * --------------------------------------------------------------
     * DELETE – por PK
     * --------------------------------------------------------------
     */
    public void delete(long idDetalleMuestraSismica) throws SQLException {
        String sql = "DELETE FROM DetalleMuestraSismica WHERE idDetalleMuestraSismica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idDetalleMuestraSismica);
            ps.executeUpdate();
        }
    }

    /*
     * --------------------------------------------------------------
     * FIND BY ID – carga completo (incluyendo TipoDeDato asociado)
     * --------------------------------------------------------------
     */
    public DetalleMuestraSismica findById(long idDetalleMuestraSismica) throws SQLException {
        String sql = "SELECT * FROM DetalleMuestraSismica WHERE idDetalleMuestraSismica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idDetalleMuestraSismica);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DetalleMuestraSismica d = new DetalleMuestraSismica();

                    d.setIdDetalleMuestraSismica(rs.getLong("idDetalleMuestraSismica"));
                    long idTipo = rs.getLong("idTipoDeDato");
                    d.setTipoDeDato(tipoDeDatoDAO.findById(idTipo));
                    d.setValor(rs.getDouble("valor"));

                    return d;
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
    public List<DetalleMuestraSismica> findAll() throws SQLException {
        String sql = "SELECT idDetalleMuestraSismica FROM DetalleMuestraSismica";
        List<DetalleMuestraSismica> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("idDetalleMuestraSismica");
                DetalleMuestraSismica d = findById(id);
                if (d != null)
                    list.add(d);
            }
        }
        return list;
    }

    /*
     * --------------------------------------------------------------
     * NUEVO: Buscar Detalles por Muestra Sísmica (para MuestraSismicaDAO)
     * --------------------------------------------------------------
     */
    public List<DetalleMuestraSismica> findByMuestraSismicaId(Connection conn, long idMuestraSismica)
            throws SQLException {
        String sql = """
                SELECT dms.idDetalleMuestraSismica
                FROM DetalleMuestraSismica dms
                JOIN MuestraSismica_DetalleMuestraSismica msd
                  ON dms.idDetalleMuestraSismica = msd.idDetalleMuestraSismica
                WHERE msd.idMuestraSismica = ?
                """;
        List<DetalleMuestraSismica> detalles = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMuestraSismica);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long idDetalle = rs.getLong("idDetalleMuestraSismica");
                    DetalleMuestraSismica d = findById(idDetalle);
                    if (d != null)
                        detalles.add(d);
                }
            }
        }
        return detalles;
    }

    public Map<Integer, Integer> getRelacionesMuestraDetalle() throws SQLException {
        Map<Integer, Integer> relaciones = new HashMap<>();
        String sql = "SELECT idDetalleMuestraSismica, idMuestraSismica FROM DetalleMuestraSismica";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                relaciones.put(rs.getInt("idDetalleMuestraSismica"), rs.getInt("idMuestraSismica"));
            }
        }
        return relaciones;
    }

}
