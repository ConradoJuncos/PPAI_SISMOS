package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Permiso;
import java.sql.*;
import java.util.*;

public class PermisoDAO {

    /* --------------------------------------------------------------
       INSERT – guarda nombre y descripción
       -------------------------------------------------------------- */
    public void insert(Permiso p) throws SQLException {
        String sql = "INSERT INTO Permiso (nombre, descripcion) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setIdPermiso(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza nombre y descripción
       -------------------------------------------------------------- */
    public void update(Permiso p) throws SQLException {
        String sql = "UPDATE Permiso SET nombre = ?, descripcion = ? WHERE idPermiso = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setLong  (3, p.getIdPermiso());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idPermiso) throws SQLException {
        String sql = "DELETE FROM Permiso WHERE idPermiso = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPermiso);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve objeto completo
       -------------------------------------------------------------- */
    public Permiso findById(long idPermiso) throws SQLException {
        String sql = "SELECT * FROM Permiso WHERE idPermiso = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idPermiso);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Permiso p = new Permiso();

                    p.setIdPermiso(rs.getLong("idPermiso"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));

                    return p;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<Permiso> findAll() throws SQLException {
        String sql = "SELECT * FROM Permiso";
        List<Permiso> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Permiso p = new Permiso();

                p.setIdPermiso(rs.getLong("idPermiso"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));

                list.add(p);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       FIND BY NOMBRE – útil para búsquedas por nombre
       -------------------------------------------------------------- */
    public Permiso findByNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM Permiso WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Permiso p = new Permiso();
                    p.setIdPermiso(rs.getLong("idPermiso"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));
                    return p;
                }
            }
        }
        return null;
    }
}