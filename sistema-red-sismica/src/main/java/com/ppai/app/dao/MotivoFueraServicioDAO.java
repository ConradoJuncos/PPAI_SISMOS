package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.MotivoFueraServicio;
import com.ppai.app.entidad.MotivoTipo;
import java.sql.*;
import java.util.*;

public class MotivoFueraServicioDAO {

    private final MotivoTipoDAO motivoTipoDAO = new MotivoTipoDAO();

    /* --------------------------------------------------------------
       INSERT – guarda comentario + id del MotivoTipo (FK)
       -------------------------------------------------------------- */
    public void insert(MotivoFueraServicio m) throws SQLException {
        String sql = "INSERT INTO MotivoFueraServicio (comentario, idMotivoTipo) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getComentario());
            ps.setLong  (2, m.getMotivoTipo().getIdMotivoTipo());  // <-- Objeto → ID

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setIdMotivoFueraServicio(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todo (incluido el MotivoTipo)
       -------------------------------------------------------------- */
    public void update(MotivoFueraServicio m) throws SQLException {
        String sql = "UPDATE MotivoFueraServicio " +
                     "SET comentario = ?, idMotivoTipo = ? " +
                     "WHERE idMotivoFueraServicio = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getComentario());
            ps.setLong  (2, m.getMotivoTipo().getIdMotivoTipo());
            ps.setLong  (3, m.getIdMotivoFueraServicio());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idMotivoFueraServicio) throws SQLException {
        String sql = "DELETE FROM MotivoFueraServicio WHERE idMotivoFueraServicio = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMotivoFueraServicio);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga objeto completo + MotivoTipo
       -------------------------------------------------------------- */
    public MotivoFueraServicio findById(long idMotivoFueraServicio) throws SQLException {
        String sql = "SELECT * FROM MotivoFueraServicio WHERE idMotivoFueraServicio = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idMotivoFueraServicio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MotivoFueraServicio m = new MotivoFueraServicio();

                    m.setIdMotivoFueraServicio(rs.getLong("idMotivoFueraServicio"));
                    m.setComentario(rs.getString("comentario"));

                    // Carga del objeto relacionado
                    long idMotivoTipo = rs.getLong("idMotivoTipo");
                    MotivoTipo motivoTipo = motivoTipoDAO.findById(idMotivoTipo);
                    m.setMotivoTipo(motivoTipo);

                    return m;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa con MotivoTipo cargado
       -------------------------------------------------------------- */
    public List<MotivoFueraServicio> findAll() throws SQLException {
        String sql = "SELECT * FROM MotivoFueraServicio";
        List<MotivoFueraServicio> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MotivoFueraServicio m = new MotivoFueraServicio();

                m.setIdMotivoFueraServicio(rs.getLong("idMotivoFueraServicio"));
                m.setComentario(rs.getString("comentario"));

                long idMotivoTipo = rs.getLong("idMotivoTipo");
                MotivoTipo motivoTipo = motivoTipoDAO.findById(idMotivoTipo);
                m.setMotivoTipo(motivoTipo);

                list.add(m);
            }
        }
        return list;
    }
}