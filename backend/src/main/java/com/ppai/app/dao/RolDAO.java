package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Rol;
import java.sql.*;
import java.util.*;

public class RolDAO {

    /* --------------------------------------------------------------
       INSERT – guarda nombre y descripción, devuelve PK autogenerada
       -------------------------------------------------------------- */
    public void insert(Rol r) throws SQLException {
        String sql = "INSERT INTO Rol (nombre, descripcion) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getNombre());
            ps.setString(2, r.getDescripcion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setIdRol(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza nombre y descripción usando PK
       -------------------------------------------------------------- */
    public void update(Rol r) throws SQLException {
        String sql = "UPDATE Rol SET nombre = ?, descripcion = ? WHERE idRol = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getNombre());
            ps.setString(2, r.getDescripcion());
            ps.setLong  (3, r.getIdRol());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idRol) throws SQLException {
        String sql = "DELETE FROM Rol WHERE idRol = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRol);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve objeto completo
       -------------------------------------------------------------- */
    public Rol findById(long idRol) throws SQLException {
        String sql = "SELECT * FROM Rol WHERE idRol = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rol r = new Rol();
                    r.setIdRol(rs.getLong("idRol"));
                    r.setNombre(rs.getString("nombre"));
                    r.setDescripcion(rs.getString("descripcion"));
                    return r;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<Rol> findAll() throws SQLException {
        String sql = "SELECT * FROM Rol";
        List<Rol> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol r = new Rol();
                r.setIdRol(rs.getLong("idRol"));
                r.setNombre(rs.getString("nombre"));
                r.setDescripcion(rs.getString("descripcion"));
                list.add(r);
            }
        }
        return list;
    }
}