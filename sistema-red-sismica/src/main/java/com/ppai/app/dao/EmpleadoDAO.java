package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Empleado;
import java.sql.*;
import java.util.*;

public class EmpleadoDAO {

    private final RolDAO rolDAO = new RolDAO();

    /*
     * --------------------------------------------------------------
     * INSERT – guarda datos del empleado + id del Rol (FK)
     * --------------------------------------------------------------
     */
    public void insert(Empleado e) throws SQLException {
        String sql = "INSERT INTO Empleado (apellido, email, nombre, telefono, idRol) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getApellido());
            ps.setString(2, e.getMail());
            ps.setString(3, e.getNombre());
            ps.setString(4, e.getTelefono());
            ps.setLong(5, e.getRol().getIdRol()); // <-- Objeto Rol → ID

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setIdEmpleado(rs.getLong(1));
                }
            }
        }
    }

    /*
     * --------------------------------------------------------------
     * UPDATE – actualiza todos los campos (incluido el Rol)
     * --------------------------------------------------------------
     */
    public void update(Empleado e) throws SQLException {
        String sql = "UPDATE Empleado " +
                "SET apellido = ?, email = ?, nombre = ?, telefono = ?, idRol = ? " +
                "WHERE idEmpleado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getApellido());
            ps.setString(2, e.getMail());
            ps.setString(3, e.getNombre());
            ps.setString(4, e.getTelefono());
            ps.setLong(5, e.getRol().getIdRol());
            ps.setLong(6, e.getIdEmpleado());

            ps.executeUpdate();
        }
    }

    /*
     * --------------------------------------------------------------
     * DELETE – por PK
     * --------------------------------------------------------------
     */
    public void delete(long idEmpleado) throws SQLException {
        String sql = "DELETE FROM Empleado WHERE idEmpleado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idEmpleado);
            ps.executeUpdate();
        }
    }

    /*
     * --------------------------------------------------------------
     * FIND BY ID – carga empleado + objeto Rol completo
     * --------------------------------------------------------------
     */
    public Empleado findById(long idEmpleado) throws SQLException {
        String sql = "SELECT * FROM Empleado WHERE idEmpleado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Empleado e = new Empleado();

                    e.setIdEmpleado(rs.getLong("idEmpleado"));
                    e.setApellido(rs.getString("apellido"));
                    e.setMail(rs.getString("email"));
                    e.setNombre(rs.getString("nombre"));
                    e.setTelefono(rs.getString("telefono"));

                    // Carga del objeto relacionado
                    long idRol = rs.getLong("idRol");
                    Rol rol = rolDAO.findById(idRol);
                    e.setRol(rol);

                    return e;
                }
            }
        }
        return null;
    }

    /*
     * --------------------------------------------------------------
     * FIND ALL – lista completa con Rol cargado
     * --------------------------------------------------------------
     */
    public List<Empleado> findAll() throws SQLException {
        String sql = "SELECT * FROM Empleado";
        List<Empleado> list = new ArrayList<>();

        // 1️⃣ Leyendo los datos basicos de empleado
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Empleado e = new Empleado();
                e.setIdEmpleado(rs.getLong("idEmpleado"));
                e.setApellido(rs.getString("apellido"));
                e.setMail(rs.getString("email"));
                e.setNombre(rs.getString("nombre"));
                e.setTelefono(rs.getString("telefono"));

                // Guardamos el idRol temporalmente
                Rol placeholder = new Rol();
                placeholder.setIdRol(rs.getLong("idRol"));
                e.setRol(placeholder);

                list.add(e);
            }
        }

        // 2️⃣ Resolviendo los objetos Rol con un nuevo acceso al DAO
        for (Empleado e : list) {
            if (e.getRol() != null && e.getRol().getIdRol() > 0) {
                Rol rol = rolDAO.findById(e.getRol().getIdRol());
                e.setRol(rol);
            }
        }

        return list;
    }

}