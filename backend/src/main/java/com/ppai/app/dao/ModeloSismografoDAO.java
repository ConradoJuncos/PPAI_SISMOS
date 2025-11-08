package com.ppai.app.dao;

import com.ppai.app.entidad.ModeloSismografo;
import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Fabricante;
import java.sql.*;
import java.util.*;

public class ModeloSismografoDAO {

    private final FabricanteDAO fabricanteDAO = new FabricanteDAO();

    // =================================================================
    // INSERT
    // =================================================================
    public void insert(ModeloSismografo m) throws SQLException {
        String sql = "INSERT INTO ModeloSismografo (caracteristicas, nombreModelo, idFabricante) " +
                     "VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getCaracteristicas());
            ps.setString(2, m.getNombre());                     // <-- getNombre() → nombreModelo
            ps.setLong(3, m.getFabricante().getIdFabricante()); // <-- Obtiene ID desde el objeto

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setIdModeloSismografo(rs.getLong(1));
                }
            }
        }
    }

    // =================================================================
    // UPDATE
    // =================================================================
    public void update(ModeloSismografo m) throws SQLException {
        String sql = "UPDATE ModeloSismografo " +
                     "SET caracteristicas = ?, nombreModelo = ?, idFabricante = ? " +
                     "WHERE idModeloSismografo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getCaracteristicas());
            ps.setString(2, m.getNombre());
            ps.setLong(3, m.getFabricante().getIdFabricante()); // <-- Usa el objeto
            ps.setLong(4, m.getIdModeloSismografo());

            ps.executeUpdate();
        }
    }

    // =================================================================
    // DELETE
    // =================================================================
    public void delete(long idModeloSismografo) throws SQLException {
        String sql = "DELETE FROM ModeloSismografo WHERE idModeloSismografo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idModeloSismografo);
            ps.executeUpdate();
        }
    }

    // =================================================================
    // FIND BY ID
    // =================================================================
    public ModeloSismografo findById(long idModeloSismografo) throws SQLException {
        String sql = "SELECT * FROM ModeloSismografo WHERE idModeloSismografo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idModeloSismografo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ModeloSismografo m = new ModeloSismografo();

                    m.setIdModeloSismografo(rs.getLong("idModeloSismografo"));
                    m.setCaracteristicas(rs.getString("caracteristicas"));
                    m.setNombre(rs.getString("nombreModelo"));

                    // Carga el objeto Fabricante desde su ID (FK)
                    long idFabricante = rs.getLong("idFabricante");
                    Fabricante fabricante = fabricanteDAO.findById(idFabricante);
                    m.setFabricante(fabricante);  // <-- Asigna el objeto completo

                    return m;
                }
            }
        }
        return null;
    }

    // =================================================================
    // FIND ALL
    // =================================================================
    public List<ModeloSismografo> findAll() throws SQLException {
        String sql = "SELECT * FROM ModeloSismografo";
        List<ModeloSismografo> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ModeloSismografo m = new ModeloSismografo();

                m.setIdModeloSismografo(rs.getLong("idModeloSismografo"));
                m.setCaracteristicas(rs.getString("caracteristicas"));
                m.setNombre(rs.getString("nombreModelo"));

                long idFabricante = rs.getLong("idFabricante");
                Fabricante fabricante = fabricanteDAO.findById(idFabricante);
                m.setFabricante(fabricante);

                list.add(m);
            }
        }
        return list;
    }
}