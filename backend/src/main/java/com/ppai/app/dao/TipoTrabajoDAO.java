package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.TipoTrabajo;
import java.sql.*;
import java.util.*;

public class TipoTrabajoDAO {

    /* --------------------------------------------------------------
       INSERT – guarda nombre y descripción
       -------------------------------------------------------------- */
    public void insert(TipoTrabajo t) throws SQLException {
        String sql = "INSERT INTO TipoTrabajo (nombre, descripcion) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDescripcion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setIdTipoTrabajo(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza nombre y descripción
       -------------------------------------------------------------- */
    public void update(TipoTrabajo t) throws SQLException {
        String sql = "UPDATE TipoTrabajo SET nombre = ?, descripcion = ? WHERE idTipoTrabajo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDescripcion());
            ps.setLong  (3, t.getIdTipoTrabajo());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idTipoTrabajo) throws SQLException {
        String sql = "DELETE FROM TipoTrabajo WHERE idTipoTrabajo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTipoTrabajo);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve objeto completo
       -------------------------------------------------------------- */
    public TipoTrabajo findById(long idTipoTrabajo) throws SQLException {
        String sql = "SELECT * FROM TipoTrabajo WHERE idTipoTrabajo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idTipoTrabajo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TipoTrabajo t = new TipoTrabajo();

                    t.setIdTipoTrabajo(rs.getLong("idTipoTrabajo"));
                    t.setNombre(rs.getString("nombre"));
                    t.setDescripcion(rs.getString("descripcion"));

                    return t;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<TipoTrabajo> findAll() throws SQLException {
        String sql = "SELECT * FROM TipoTrabajo";
        List<TipoTrabajo> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TipoTrabajo t = new TipoTrabajo();

                t.setIdTipoTrabajo(rs.getLong("idTipoTrabajo"));
                t.setNombre(rs.getString("nombre"));
                t.setDescripcion(rs.getString("descripcion"));

                list.add(t);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       FIND BY NOMBRE – útil para búsquedas por nombre
       -------------------------------------------------------------- */
    public TipoTrabajo findByNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM TipoTrabajo WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TipoTrabajo t = new TipoTrabajo();
                    t.setIdTipoTrabajo(rs.getLong("idTipoTrabajo"));
                    t.setNombre(rs.getString("nombre"));
                    t.setDescripcion(rs.getString("descripcion"));
                    return t;
                }
            }
        }
        return null;
    }
}