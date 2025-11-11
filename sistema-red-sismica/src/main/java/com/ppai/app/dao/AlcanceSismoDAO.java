package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.AlcanceSismo;
import java.sql.*;
import java.util.*;

public class AlcanceSismoDAO {
    public void insert(AlcanceSismo a) throws SQLException {
        String sql = "INSERT INTO AlcanceSismo (descripcion, nombre) VALUES (?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getDescripcion());
            ps.setString(2, a.getNombre());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setIdAlcanceSismo(rs.getLong(1));
            }
        }
    }

    public void update(AlcanceSismo a) throws SQLException {
        String sql = "UPDATE AlcanceSismo SET descripcion = ?, nombre = ? WHERE idAlcanceSismo = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getDescripcion());
            ps.setString(2, a.getNombre());
            ps.setLong(3, a.getIdAlcanceSismo());
            ps.executeUpdate();
        }
    }

    public void delete(long idAlcanceSismo) throws SQLException {
        String sql = "DELETE FROM AlcanceSismo WHERE idAlcanceSismo = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idAlcanceSismo);
            ps.executeUpdate();
        }
    }

    public AlcanceSismo findById(long idAlcanceSismo) throws SQLException {
        String sql = "SELECT * FROM AlcanceSismo WHERE idAlcanceSismo = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idAlcanceSismo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AlcanceSismo a = new AlcanceSismo();
                    a.setIdAlcanceSismo(rs.getLong("idAlcanceSismo"));
                    a.setDescripcion(rs.getString("descripcion"));
                    a.setNombre(rs.getString("nombre"));
                    return a;
                }
            }
        }
        return null;
    }

    public List<AlcanceSismo> findAll() throws SQLException {
        String sql = "SELECT * FROM AlcanceSismo";
        List<AlcanceSismo> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AlcanceSismo a = new AlcanceSismo();
                a.setIdAlcanceSismo(rs.getLong("idAlcanceSismo"));
                a.setDescripcion(rs.getString("descripcion"));
                a.setNombre(rs.getString("nombre"));
                list.add(a);
            }
        }
        return list;
    }
}