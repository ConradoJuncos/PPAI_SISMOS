package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Usuario;
import com.ppai.app.entidad.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    public Usuario findById(long id) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE idUsuario = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public Usuario findByNombreUsuario(String nombre) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE nombreUsuario = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Usuario> findAll() throws SQLException {
        String sql = "SELECT * FROM Usuario";
        List<Usuario> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void insert(Usuario u) throws SQLException {
        String sql = "INSERT INTO Usuario (idUsuario, contraseña, nombreUsuario, idEmpleado) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, u.getIdUsuario());
            ps.setString(2, u.getContraseña());
            ps.setString(3, u.getNombreUsuario());
            ps.setLong(4, u.getEmpleado().getIdEmpleado());
            ps.executeUpdate();
        }
    }

    public void update(Usuario u) throws SQLException {
        String sql = "UPDATE Usuario SET contraseña = ?, nombreUsuario = ?, idEmpleado = ? WHERE idUsuario = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getContraseña());
            ps.setString(2, u.getNombreUsuario());
            ps.setLong(3, u.getEmpleado().getIdEmpleado());
            ps.setLong(4, u.getIdUsuario());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM Usuario WHERE idUsuario = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Usuario map(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getLong("idUsuario"));
        u.setContraseña(rs.getString("contraseña"));
        u.setNombreUsuario(rs.getString("nombreUsuario"));
        long idEmp = rs.getLong("idEmpleado");
        Empleado e = empleadoDAO.findById(idEmp);
        u.setEmpleado(e);
        return u;
    }
}

