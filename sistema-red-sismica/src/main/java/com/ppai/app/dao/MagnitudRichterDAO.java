package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.MagnitudRichter;
import java.sql.*;
import java.util.*;

public class MagnitudRichterDAO {

    /* --------------------------------------------------------------
       INSERT – guarda la magnitud
       -------------------------------------------------------------- */
    public void insert(MagnitudRichter m) throws SQLException {
        String sql = "INSERT INTO MagnitudRichter (numero, descripcion) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt   (1, m.getNumero());
            ps.setString(2, m.getDescripcion());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza descripción
       -------------------------------------------------------------- */
    public void update(MagnitudRichter m) throws SQLException {
        String sql = "UPDATE MagnitudRichter SET descripcion = ? WHERE numero = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getDescripcion());
            ps.setInt   (2, m.getNumero());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por número (PK)
       -------------------------------------------------------------- */
    public void delete(int numero) throws SQLException {
        String sql = "DELETE FROM MagnitudRichter WHERE numero = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numero);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY NUMERO – ¡EL QUE NECESITAS!
       -------------------------------------------------------------- */
    public MagnitudRichter findByNumero(int numero) throws SQLException {
        String sql = "SELECT * FROM MagnitudRichter WHERE numero = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MagnitudRichter m = new MagnitudRichter();
                    m.setNumero(rs.getInt("numero"));
                    m.setDescripcion(rs.getString("descripcion"));
                    return m;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL
       -------------------------------------------------------------- */
    public List<MagnitudRichter> findAll() throws SQLException {
        String sql = "SELECT * FROM MagnitudRichter";
        List<MagnitudRichter> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MagnitudRichter m = new MagnitudRichter();
                m.setNumero(rs.getInt("numero"));
                m.setDescripcion(rs.getString("descripcion"));
                list.add(m);
            }
        }
        return list;
    }
}