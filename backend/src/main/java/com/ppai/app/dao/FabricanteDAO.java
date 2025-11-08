package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Fabricante;
import java.sql.*;
import java.util.*;

public class FabricanteDAO {

    public void insert(Fabricante f) throws SQLException {
        String sql = "INSERT INTO Fabricante (nombre) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getNombre());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    f.setIdFabricante(rs.getLong(1));
                }
            }
        }
    }

    public void update(Fabricante f) throws SQLException {
        String sql = "UPDATE Fabricante SET nombre = ? WHERE idFabricante = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getNombre());
            ps.setLong(2, f.getIdFabricante());
            ps.executeUpdate();
        }
    }

    public void delete(long idFabricante) throws SQLException {
        String sql = "DELETE FROM Fabricante WHERE idFabricante = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idFabricante);
            ps.executeUpdate();
        }
    }

    public Fabricante findById(long idFabricante) throws SQLException {
        String sql = "SELECT * FROM Fabricante WHERE idFabricante = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idFabricante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Fabricante f = new Fabricante();
                    f.setIdFabricante(rs.getLong("idFabricante"));
                    f.setNombre(rs.getString("nombre"));
                    return f;
                }
            }
        }
        return null;
    }

    public List<Fabricante> findAll() throws SQLException {
        String sql = "SELECT * FROM Fabricante";
        List<Fabricante> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Fabricante f = new Fabricante();
                f.setIdFabricante(rs.getLong("idFabricante"));
                f.setNombre(rs.getString("nombre"));
                list.add(f);
            }
        }
        return list;
    }
}