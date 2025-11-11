package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.DetalleMuestraSismica;
import java.sql.*;
import java.util.*;

public class DetalleMuestraSismicaDAO {

    private final TipoDeDatoDAO tipoDeDatoDAO = new TipoDeDatoDAO();

    /* --------------------------------------------------------------
       INSERT – guarda todos los campos
       -------------------------------------------------------------- */
    public void insert(DetalleMuestraSismica d) throws SQLException {
        String sql = "INSERT INTO DetalleMuestraSismica (idTipoDeDato, valor) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong  (1, d.getIdTipoDeDato());
            ps.setDouble(2, d.getValor());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    d.setIdDetalleMuestraSismica(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todo
       -------------------------------------------------------------- */
    public void update(DetalleMuestraSismica d) throws SQLException {
        String sql = "UPDATE DetalleMuestraSismica SET idTipoDeDato = ?, valor = ? WHERE idDetalleMuestraSismica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong  (1, d.getIdTipoDeDato());
            ps.setDouble(2, d.getValor());
            ps.setLong  (3, d.getIdDetalleMuestraSismica());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idDetalleMuestraSismica) throws SQLException {
        String sql = "DELETE FROM DetalleMuestraSismica WHERE idDetalleMuestraSismica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idDetalleMuestraSismica);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga completo (incluyendo TipoDeDato si es necesario)
       -------------------------------------------------------------- */
    public DetalleMuestraSismica findById(long idDetalleMuestraSismica) throws SQLException {
        String sql = "SELECT * FROM DetalleMuestraSismica WHERE idDetalleMuestraSismica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idDetalleMuestraSismica);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DetalleMuestraSismica d = new DetalleMuestraSismica();

                    d.setIdDetalleMuestraSismica(rs.getLong("idDetalleMuestraSismica"));
                    d.setIdTipoDeDato(rs.getLong("idTipoDeDato"));
                    d.setValor(rs.getDouble("valor"));

                    return d;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<DetalleMuestraSismica> findAll() throws SQLException {
        String sql = "SELECT * FROM DetalleMuestraSismica";
        List<DetalleMuestraSismica> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DetalleMuestraSismica d = new DetalleMuestraSismica();

                d.setIdDetalleMuestraSismica(rs.getLong("idDetalleMuestraSismica"));
                d.setIdTipoDeDato(rs.getLong("idTipoDeDato"));
                d.setValor(rs.getDouble("valor"));

                list.add(d);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       NUEVO: Buscar Detalles por Muestra Sísmica (para MuestraSismicaDAO)
       -------------------------------------------------------------- */
    public List<DetalleMuestraSismica> findByMuestraSismicaId(Connection conn, long idMuestraSismica) throws SQLException {
        String sql = """
            SELECT dms.* FROM DetalleMuestraSismica dms
            JOIN MuestraSismica_Detalle msd ON dms.idDetalleMuestraSismica = msd.idDetalleMuestraSismica
            WHERE msd.idMuestraSismica = ?
            """;
        List<DetalleMuestraSismica> detalles = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMuestraSismica);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleMuestraSismica d = new DetalleMuestraSismica();

                    d.setIdDetalleMuestraSismica(rs.getLong("idDetalleMuestraSismica"));
                    d.setIdTipoDeDato(rs.getLong("idTipoDeDato"));
                    d.setValor(rs.getDouble("valor"));

                    detalles.add(d);
                }
            }
        }
        return detalles;
    }
}