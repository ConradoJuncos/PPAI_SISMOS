package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.ApreciacionTipo;
import java.sql.*;
import java.util.*;

public class ApreciacionTipoDAO {

    /* --------------------------------------------------------------
       INSERT – guarda color y leyenda
       -------------------------------------------------------------- */
    public void insert(ApreciacionTipo a) throws SQLException {
        String sql = "INSERT INTO ApreciacionTipo (color, leyenda) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getColor());
            ps.setString(2, a.getLeyenda());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    a.setIdApreciacionTipo(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza color y leyenda
       -------------------------------------------------------------- */
    public void update(ApreciacionTipo a) throws SQLException {
        String sql = "UPDATE ApreciacionTipo SET color = ?, leyenda = ? WHERE idApreciacionTipo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getColor());
            ps.setString(2, a.getLeyenda());
            ps.setLong  (3, a.getIdApreciacionTipo());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idApreciacionTipo) throws SQLException {
        String sql = "DELETE FROM ApreciacionTipo WHERE idApreciacionTipo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idApreciacionTipo);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve objeto completo
       -------------------------------------------------------------- */
    public ApreciacionTipo findById(long idApreciacionTipo) throws SQLException {
        String sql = "SELECT * FROM ApreciacionTipo WHERE idApreciacionTipo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idApreciacionTipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ApreciacionTipo a = new ApreciacionTipo();

                    a.setIdApreciacionTipo(rs.getLong("idApreciacionTipo"));
                    a.setColor(rs.getString("color"));
                    a.setLeyenda(rs.getString("leyenda"));

                    return a;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<ApreciacionTipo> findAll() throws SQLException {
        String sql = "SELECT * FROM ApreciacionTipo";
        List<ApreciacionTipo> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ApreciacionTipo a = new ApreciacionTipo();

                a.setIdApreciacionTipo(rs.getLong("idApreciacionTipo"));
                a.setColor(rs.getString("color"));
                a.setLeyenda(rs.getString("leyenda"));

                list.add(a);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       NUEVO: Buscar Apreciaciones por Tarea Asignada (para TareaAsignadaDAO)
       -------------------------------------------------------------- */
    public List<ApreciacionTipo> findByTareaAsignadaId(Connection conn, long idTareaAsignada) throws SQLException {
        String sql = """
            SELECT at.* FROM ApreciacionTipo at
            JOIN TareaAsignada_Apreciacion taa ON at.idApreciacionTipo = taa.idApreciacion
            WHERE taa.idTareaAsignada = ?
            """;
        List<ApreciacionTipo> apreciaciones = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTareaAsignada);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ApreciacionTipo a = new ApreciacionTipo();
                    a.setIdApreciacionTipo(rs.getLong("idApreciacionTipo"));
                    a.setColor(rs.getString("color"));
                    a.setLeyenda(rs.getString("leyenda"));
                    apreciaciones.add(a);
                }
            }
        }
        return apreciaciones;
    }
}