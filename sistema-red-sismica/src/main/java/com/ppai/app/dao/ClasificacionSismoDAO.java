package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.ClasificacionSismo;
import java.sql.*;
import java.util.*;

public class ClasificacionSismoDAO {

    public void insert(ClasificacionSismo c) throws SQLException {
        String sql = "INSERT INTO ClasificacionSismo (kmProfundidadDesde, kmProfundidadHasta, nombre) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, c.getKmProfundidadDesde());
            ps.setDouble(2, c.getKmProfundidadHasta());
            ps.setString(3, c.getNombre());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setIdClasificacionSismo(rs.getLong(1));
                }
            }
        }
    }

    public void update(ClasificacionSismo c) throws SQLException {
        String sql = "UPDATE ClasificacionSismo SET kmProfundidadDesde = ?, kmProfundidadHasta = ?, nombre = ? WHERE idClasificacionSismo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, c.getKmProfundidadDesde());
            ps.setDouble(2, c.getKmProfundidadHasta());
            ps.setString(3, c.getNombre());
            ps.setLong(4, c.getIdClasificacionSismo());
            ps.executeUpdate();
        }
    }

    public void delete(long idClasificacionSismo) throws SQLException {
        String sql = "DELETE FROM ClasificacionSismo WHERE idClasificacionSismo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idClasificacionSismo);
            ps.executeUpdate();
        }
    }

    public ClasificacionSismo findById(long idClasificacionSismo) throws SQLException {
        String sql = "SELECT * FROM ClasificacionSismo WHERE idClasificacionSismo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idClasificacionSismo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ClasificacionSismo c = new ClasificacionSismo();
                    c.setIdClasificacionSismo(rs.getLong("idClasificacionSismo"));
                    c.setKmProfundidadDesde(rs.getDouble("kmProfundidadDesde"));
                    c.setKmProfundidadHasta(rs.getDouble("kmProfundidadHasta"));
                    c.setNombre(rs.getString("nombre"));
                    return c;
                }
            }
        }
        return null;
    }

    public List<ClasificacionSismo> findAll() throws SQLException {
        String sql = "SELECT * FROM ClasificacionSismo";
        List<ClasificacionSismo> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ClasificacionSismo c = new ClasificacionSismo();
                c.setIdClasificacionSismo(rs.getLong("idClasificacionSismo"));
                c.setKmProfundidadDesde(rs.getDouble("kmProfundidadDesde"));
                c.setKmProfundidadHasta(rs.getDouble("kmProfundidadHasta"));
                c.setNombre(rs.getString("nombre"));
                list.add(c);
            }
        }
        return list;
    }
}