package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Estado;
import java.sql.*;
import java.util.*;

public class EstadoDAO {

    /* --------------------------------------------------------------
       INSERT – guarda ámbito y nombreEstado, devuelve PK autogenerada
       -------------------------------------------------------------- */
    public void insert(Estado e) throws SQLException {
        String sql = "INSERT INTO Estado (ambito, nombreEstado) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getAmbito());
            ps.setString(2, e.getNombreEstado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setIdEstado(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza ámbito y nombreEstado usando PK
       -------------------------------------------------------------- */
    public void update(Estado e) throws SQLException {
        String sql = "UPDATE Estado SET ambito = ?, nombreEstado = ? WHERE idEstado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getAmbito());
            ps.setString(2, e.getNombreEstado());
            ps.setLong  (3, e.getIdEstado());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idEstado) throws SQLException {
        String sql = "DELETE FROM Estado WHERE idEstado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idEstado);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve instancia de Estado (subclase concreta)
       -------------------------------------------------------------- */
    public Estado findById(long idEstado) throws SQLException {
        String sql = "SELECT * FROM Estado WHERE idEstado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idEstado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombreEstado = rs.getString("nombreEstado");

                    // Instanciar la subclase concreta según nombreEstado
                    return instanciarEstado(rs, nombreEstado);
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa de todos los estados (concreto)
       -------------------------------------------------------------- */
    public List<Estado> findAll() throws SQLException {
        String sql = "SELECT * FROM Estado";
        List<Estado> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nombreEstado = rs.getString("nombreEstado");
                Estado e = instanciarEstado(rs, nombreEstado);
                list.add(e);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       MÉTODO PRIVADO: Instancia la subclase concreta según nombreEstado
       -------------------------------------------------------------- */
    private Estado instanciarEstado(ResultSet rs, String nombreEstado) throws SQLException {
        try {
            // Construir nombre completo de la clase: com.ppai.app.entidad.EstadoX
            String className = "com.ppai.app.entidad.Estado" + nombreEstado.replaceAll("\\s+", "");
            Class<?> clazz = Class.forName(className);
            Estado estado = (Estado) clazz.getDeclaredConstructor().newInstance();

            estado.setIdEstado(rs.getLong("idEstado"));
            estado.setAmbito(rs.getString("ambito"));
            estado.setNombreEstado(rs.getString("nombreEstado"));

            return estado;

        } catch (Exception ex) {
            throw new SQLException("No se pudo instanciar el estado: " + nombreEstado, ex);
        }
    }
}