package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.TipoDeDato;
import java.sql.*;
import java.util.*;

public class TipoDeDatoDAO {

    /* --------------------------------------------------------------
       INSERT – guarda todos los campos, devuelve PK autogenerada
       -------------------------------------------------------------- */
    public void insert(TipoDeDato t) throws SQLException {
        String sql = "INSERT INTO TipoDeDato (denominacion, nombreUnidadMedida, valorUmbral) " +
                     "VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getDenominacion());
            ps.setString(2, t.getnombreUnidadMedida());  // <-- nombre exacto del getter
            ps.setDouble(3, t.getValorUmbral());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setIdTipoDeDato(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todos los campos usando PK
       -------------------------------------------------------------- */
    public void update(TipoDeDato t) throws SQLException {
        String sql = "UPDATE TipoDeDato " +
                     "SET denominacion = ?, nombreUnidadMedida = ?, valorUmbral = ? " +
                     "WHERE idTipoDeDato = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getDenominacion());
            ps.setString(2, t.getnombreUnidadMedida());
            ps.setDouble(3, t.getValorUmbral());
            ps.setLong  (4, t.getIdTipoDeDato());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idTipoDeDato) throws SQLException {
        String sql = "DELETE FROM TipoDeDato WHERE idTipoDeDato = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTipoDeDato);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – devuelve objeto completo
       -------------------------------------------------------------- */
    public TipoDeDato findById(long idTipoDeDato) throws SQLException {
        String sql = "SELECT * FROM TipoDeDato WHERE idTipoDeDato = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idTipoDeDato);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TipoDeDato t = new TipoDeDato();

                    t.setIdTipoDeDato(rs.getLong("idTipoDeDato"));
                    t.setDenominacion(rs.getString("denominacion"));
                    t.setnombreUnidadMedida(rs.getString("nombreUnidadMedida"));
                    t.setValorUmbral(rs.getDouble("valorUmbral"));

                    return t;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa
       -------------------------------------------------------------- */
    public List<TipoDeDato> findAll() throws SQLException {
        String sql = "SELECT * FROM TipoDeDato";
        List<TipoDeDato> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TipoDeDato t = new TipoDeDato();

                t.setIdTipoDeDato(rs.getLong("idTipoDeDato"));
                t.setDenominacion(rs.getString("denominacion"));
                t.setnombreUnidadMedida(rs.getString("nombreUnidadMedida"));
                t.setValorUmbral(rs.getDouble("valorUmbral"));

                list.add(t);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       MÉTODO EXTRA: buscar por denominación (usando el método de la clase)
       -------------------------------------------------------------- */
    public TipoDeDato findByDenominacion(String denominacion) throws SQLException {
        List<TipoDeDato> todos = findAll();
        for (TipoDeDato t : todos) {
            if (t.esTuDenominacion(denominacion)) {
                return t;
            }
        }
        return null;
    }
}