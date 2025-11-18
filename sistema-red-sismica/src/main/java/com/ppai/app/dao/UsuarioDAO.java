package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Usuario;
import com.ppai.app.entidad.Empleado;
import com.ppai.app.entidad.Suscripcion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Usuario.
 * Corrige inconsistencias de columnas, FK a Empleado y evita conexiones
 * anidadas.
 */
public class UsuarioDAO {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final PerfilDAO perfilDAO = new PerfilDAO();
    private final SuscripcionDAO suscripcionDAO = new SuscripcionDAO();

    /*
     * ==============================================================
     * INSERT
     * ==============================================================
     */
    public void insert(Usuario u) throws SQLException {
        String sql = """
                    INSERT INTO Usuario (nombreUsuario, contraseña, idEmpleado)
                    VALUES (?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getContraseña());
            ps.setLong(3, u.getEmpleado().getIdEmpleado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    u.setIdUsuario(rs.getLong(1));
                }
            }
        }
    }

    /*
     * ==============================================================
     * UPDATE
     * ==============================================================
     */
    public void update(Usuario u) throws SQLException {
        String sql = """
                    UPDATE Usuario
                    SET nombreUsuario = ?, contraseña = ?, idEmpleado = ?
                    WHERE idUsuario = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getContraseña());
            ps.setLong(3, u.getEmpleado().getIdEmpleado());
            ps.setLong(4, u.getIdUsuario());
            ps.executeUpdate();
        }
    }

    /*
     * ==============================================================
     * DELETE
     * ==============================================================
     */
    public void delete(long idUsuario) throws SQLException {
        String sql = "DELETE FROM Usuario WHERE idUsuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            ps.executeUpdate();
        }
    }

    /*
     * ==============================================================
     * FIND BY ID
     * ==============================================================
     */
    public Usuario findById(long idUsuario) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE idUsuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getLong("idUsuario"));
                    u.setNombreUsuario(rs.getString("nombreUsuario"));
                    u.setContraseña(rs.getString("contraseña"));

                    // Cargar empleado (FK obligatoria)
                    long idEmpleado = rs.getLong("idEmpleado");
                    Empleado empleado = empleadoDAO.findById(idEmpleado);
                    u.setEmpleado(empleado);

                    // Cargar relaciones N:N (cada DAO maneja su propia conexión)
                    u.setPerfil(findPerfilesByUsuario(idUsuario));
                    u.setSuscripcion(findSuscripcionesByUsuario(idUsuario));

                    return u;
                }
            }
        }
        return null;
    }

    /*
     * ==============================================================
     * FIND ALL (SOLUCIÓN)
     * ==============================================================
     */
    public List<Usuario> findAll() throws SQLException {
        String sql = "SELECT * FROM Usuario";
        List<Usuario> usuarios = new ArrayList<>();
        // Mantenemos una lista temporal de IDs de Empleado para la carga diferida
        List<Long> idEmpleados = new ArrayList<>();
        // Almacenamos los IDs de Usuario para las relaciones N:N
        List<Long> idUsuarios = new ArrayList<>();

        // 1. CARGA DE DATOS ESCALARES Y RECUPERACIÓN DE IDs
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) { // rs, ps, conn se cierran aquí automáticamente

            while (rs.next()) {
                Usuario u = new Usuario();
                long idUsuario = rs.getLong("idUsuario");
                long idEmpleado = rs.getLong("idEmpleado");

                u.setIdUsuario(idUsuario);
                u.setNombreUsuario(rs.getString("nombreUsuario"));
                u.setContraseña(rs.getString("contraseña"));

                // Guardamos IDs para la carga diferida
                idEmpleados.add(idEmpleado);
                idUsuarios.add(idUsuario);
                usuarios.add(u);
            }
        }
        // FIN del primer try-with-resources. TODOS los recursos están cerrados.

        // 2. CARGA DIFERIDA (Lazy Loading) de Empleados y Relaciones N:N
        // Aquí puedes llamar de forma segura a otros DAOs que abren/cierran sus propias
        // conexiones.

        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            long currentIdUsuario = idUsuarios.get(i);
            long currentIdEmpleado = idEmpleados.get(i);

            // Cargar Empleado (1:N)
            Empleado empleado = empleadoDAO.findById(currentIdEmpleado);
            u.setEmpleado(empleado);

            // Cargar Perfiles (N:N)
            u.setPerfil(findPerfilesByUsuario(currentIdUsuario));

            // Cargar Suscripciones (N:N)
            u.setSuscripcion(findSuscripcionesByUsuario(currentIdUsuario));
        }

        return usuarios;
    }

    /*
     * ==============================================================
     * AUXILIARES: RELACIÓN N:N Usuario–Perfil
     * ==============================================================
     */
    private List<Perfil> findPerfilesByUsuario(long idUsuario) throws SQLException {
        String sql = """
                    SELECT p.idPerfil
                    FROM Usuario_Perfil up
                    JOIN Perfil p ON p.idPerfil = up.idPerfil
                    WHERE up.idUsuario = ?
                """;

        List<Perfil> perfiles = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long idPerfil = rs.getLong("idPerfil");
                    Perfil p = perfilDAO.findById(idPerfil);
                    if (p != null)
                        perfiles.add(p);
                }
            }
        }
        return perfiles;
    }

    /*
     * ==============================================================
     * AUXILIARES: RELACIÓN N:N Usuario–Suscripción
     * ==============================================================
     */
    private List<Suscripcion> findSuscripcionesByUsuario(long idUsuario) throws SQLException {
        return suscripcionDAO.findByUsuarioId(idUsuario);
    }
}
