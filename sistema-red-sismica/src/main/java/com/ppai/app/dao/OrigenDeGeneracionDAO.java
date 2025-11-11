package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.OrigenDeGeneracion;
import java.sql.*;
import java.util.*;

public class OrigenDeGeneracionDAO {

    /* --------------------------------------------------------------
       INSERT – guarda descripción y nombre, devuelve PK autogenerada
       -------------------------------------------------------------- */
    public void insert(OrigenDeGeneracion o) throws SQLException {
        String sql = "INSERT INTO OrigenDeGeneracion (descripcion, nombre) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, o.getDescripcion());
            ps.setString(2, o.getNombre());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    o.setIdOrigenDeGeneracion(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza descripción y nombre usando PK
       -------------------------------------------------------------- */
    public void update(OrigenDeGeneracion o) throws SQLException {
        String sql = "UPDATE OrigenDeGeneracion SET descripcion = ?, nombre = ? WHERE idOrigenDeGeneracion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, o.getDescripcion());
            ps.setString(2, o.getNombre());
            ps.setLong  (3, o.getOrigenDeGeneracion());  // <-- getOrigenDeGeneracion() devuelve el ID

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idOrigenDeGeneracion) throws SQLException {
        String sql = "DELETE FROM OrigenDeGeneracion WHERE idOrigenDeGeneracion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idOrigenDeGeneracion);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve objeto completo
       -------------------------------------------------------------- */
    public OrigenDeGeneracion findById(long idOrigenDeGeneracion) throws SQLException {
        String sql = "SELECT * FROM OrigenDeGeneracion WHERE idOrigenDeGeneracion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idOrigenDeGeneracion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OrigenDeGeneracion o = new OrigenDeGeneracion();
                    o.setIdOrigenDeGeneracion(rs.getLong("idOrigenDeGeneracion"));
                    o.setDescripcion(rs.getString("descripcion"));
                    o.setNombre(rs.getString("nombre"));
                    return o;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<OrigenDeGeneracion> findAll() throws SQLException {
        String sql = "SELECT * FROM OrigenDeGeneracion";
        List<OrigenDeGeneracion> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                OrigenDeGeneracion o = new OrigenDeGeneracion();
                o.setIdOrigenDeGeneracion(rs.getLong("idOrigenDeGeneracion"));
                o.setDescripcion(rs.getString("descripcion"));
                o.setNombre(rs.getString("nombre"));
                list.add(o);
            }
        }
        return list;
    }
}