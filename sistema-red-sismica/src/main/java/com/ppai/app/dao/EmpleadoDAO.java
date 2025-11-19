package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    public Empleado findById(long id) throws SQLException {
        String sql = "SELECT * FROM Empleado WHERE idEmpleado = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Empleado> findAll() throws SQLException {
        String sql = "SELECT * FROM Empleado";
        List<Empleado> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void insert(Empleado e) throws SQLException {
        String sql = "INSERT INTO Empleado (idEmpleado, apellido, mail, nombre, telefono) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, e.getIdEmpleado());
            ps.setString(2, e.getApellido());
            ps.setString(3, e.getMail());
            ps.setString(4, e.getNombre());
            ps.setString(5, e.getTelefono());
            ps.executeUpdate();
        }
    }

    public void update(Empleado e) throws SQLException {
        String sql = "UPDATE Empleado SET apellido = ?, mail = ?, nombre = ?, telefono = ? WHERE idEmpleado = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getApellido());
            ps.setString(2, e.getMail());
            ps.setString(3, e.getNombre());
            ps.setString(4, e.getTelefono());
            ps.setLong(5, e.getIdEmpleado());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM Empleado WHERE idEmpleado = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Empleado map(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setIdEmpleado(rs.getLong("idEmpleado"));
        e.setApellido(rs.getString("apellido"));
        e.setMail(rs.getString("mail"));
        e.setNombre(rs.getString("nombre"));
        e.setTelefono(rs.getString("telefono"));
        return e;
    }
}

