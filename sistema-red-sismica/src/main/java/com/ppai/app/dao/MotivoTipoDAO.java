package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.MotivoTipo;
import java.sql.*;
import java.util.*;

public class MotivoTipoDAO {

    /* --------------------------------------------------------------
       INSERT – guarda descripción, devuelve PK autogenerada
       -------------------------------------------------------------- */
    public void insert(MotivoTipo mt) throws SQLException {
        String sql = "INSERT INTO MotivoTipo (descripcion) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, mt.getDescripcion());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    mt.setIdMotivoTipo(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza descripción usando PK
       -------------------------------------------------------------- */
    public void update(MotivoTipo mt) throws SQLException {
        String sql = "UPDATE MotivoTipo SET descripcion = ? WHERE idMotivoTipo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mt.getDescripcion());
            ps.setLong  (2, mt.getIdMotivoTipo());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idMotivoTipo) throws SQLException {
        String sql = "DELETE FROM MotivoTipo WHERE idMotivoTipo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMotivoTipo);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve objeto con todos los datos
       -------------------------------------------------------------- */
    public MotivoTipo findById(long idMotivoTipo) throws SQLException {
        String sql = "SELECT * FROM MotivoTipo WHERE idMotivoTipo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idMotivoTipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MotivoTipo mt = new MotivoTipo();
                    mt.setIdMotivoTipo(rs.getLong("idMotivoTipo"));
                    mt.setDescripcion(rs.getString("descripcion"));
                    return mt;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<MotivoTipo> findAll() throws SQLException {
        String sql = "SELECT * FROM MotivoTipo";
        List<MotivoTipo> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MotivoTipo mt = new MotivoTipo();
                mt.setIdMotivoTipo(rs.getLong("idMotivoTipo"));
                mt.setDescripcion(rs.getString("descripcion"));
                list.add(mt);
            }
        }
        return list;
    }
}