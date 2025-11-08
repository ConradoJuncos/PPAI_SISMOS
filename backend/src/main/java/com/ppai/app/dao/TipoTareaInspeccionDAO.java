package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.TipoTareaInspeccion;
import java.sql.*;
import java.util.*;

public class TipoTareaInspeccionDAO {

    /* --------------------------------------------------------------
       INSERT – guarda todos los campos
       -------------------------------------------------------------- */
    public void insert(TipoTareaInspeccion t) throws SQLException {
        String sql = """
            INSERT INTO TipoTareaInspeccion 
            (nombre, descripcionTrabajo, duracionEstimada)
            VALUES (?, ?, ?)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDescripcionTrabajo());
            ps.setString(3, t.getDuracionEstimada());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setCodigo(rs.getLong(1)); // ← PK asignada
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todo
       -------------------------------------------------------------- */
    public void update(TipoTareaInspeccion t) throws SQLException {
        String sql = """
            UPDATE TipoTareaInspeccion SET 
            nombre = ?, descripcionTrabajo = ?, duracionEstimada = ?
            WHERE codigo = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDescripcionTrabajo());
            ps.setString(3, t.getDuracionEstimada());
            ps.setLong  (4, t.getCodigo());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long codigo) throws SQLException {
        String sql = "DELETE FROM TipoTareaInspeccion WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, codigo);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve objeto completo
       -------------------------------------------------------------- */
    public TipoTareaInspeccion findById(long codigo) throws SQLException {
        String sql = "SELECT * FROM TipoTareaInspeccion WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TipoTareaInspeccion t = new TipoTareaInspeccion();

                    t.setCodigo(rs.getLong("codigo"));
                    t.setNombre(rs.getString("nombre"));
                    t.setDescripcionTrabajo(rs.getString("descripcionTrabajo"));
                    t.setDuracionEstimada(rs.getString("duracionEstimada"));

                    return t;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<TipoTareaInspeccion> findAll() throws SQLException {
        String sql = "SELECT * FROM TipoTareaInspeccion";
        List<TipoTareaInspeccion> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TipoTareaInspeccion t = new TipoTareaInspeccion();

                t.setCodigo(rs.getLong("codigo"));
                t.setNombre(rs.getString("nombre"));
                t.setDescripcionTrabajo(rs.getString("descripcionTrabajo"));
                t.setDuracionEstimada(rs.getString("duracionEstimada"));

                list.add(t);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       FIND BY NOMBRE – útil para búsquedas
       -------------------------------------------------------------- */
    public TipoTareaInspeccion findByNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM TipoTareaInspeccion WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TipoTareaInspeccion t = new TipoTareaInspeccion();
                    t.setCodigo(rs.getLong("codigo"));
                    t.setNombre(rs.getString("nombre"));
                    t.setDescripcionTrabajo(rs.getString("descripcionTrabajo"));
                    t.setDuracionEstimada(rs.getString("duracionEstimada"));
                    return t;
                }
            }
        }
        return null;
    }
}