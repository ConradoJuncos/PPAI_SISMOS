package com.ppai.app.dao;

import com.ppai.app.entidad.Usuario;
import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Empleado;
import com.ppai.app.entidad.Perfil;
import com.ppai.app.entidad.Suscripcion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Usuario.
 * Maneja las operaciones CRUD y las relaciones con Empleado, Perfil y Suscripcion.
 */
public class UsuarioDAO {

    // DAOs para las entidades relacionadas
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final PerfilDAO perfilDAO = new PerfilDAO();
    // Requerimos SuscripcionDAO, ya generado previamente.
    private final SuscripcionDAO suscripcionDAO = new SuscripcionDAO(); 

    // Asumimos que DatabaseConnection y SuscripcionDAO existen y están implementados.

    /* --------------------------------------------------------------
       INSERT – guarda datos principales + relaciones N:N
       -------------------------------------------------------------- */
    public void insert(Usuario u) throws SQLException {
        // SQL para la tabla principal 'Usuario'
        String sql = "INSERT INTO Usuario (contraseña, nombreUsuario, idEmpleado) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getContraseña());
            ps.setString(2, u.getNombreUsuario());
            // Asume que el objeto Empleado ya tiene su ID persistido (idEmpleado).
            ps.setLong(3, u.getEmpleado().getIdEmpleado()); 

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idUsuario = rs.getLong(1);
                    u.setIdUsuario(idUsuario);

                    // Persistir relaciones N:N
                    insertPerfiles(conn, idUsuario, u.getPerfil());
                    insertSuscripciones(conn, idUsuario, u.getSuscripcion());
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todo (incluyendo relaciones N:N)
       -------------------------------------------------------------- */
    public void update(Usuario u) throws SQLException {
        String sql = "UPDATE Usuario SET contraseña = ?, nombreUsuario = ?, idEmpleado = ? WHERE idUsuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getContraseña());
            ps.setString(2, u.getNombreUsuario());
            ps.setLong(3, u.getEmpleado().getIdEmpleado());
            ps.setLong(4, u.getIdUsuario());

            ps.executeUpdate();

            // Actualizar relaciones N:N (borrar existentes e insertar nuevas)
            deletePerfiles(conn, u.getIdUsuario());
            deleteSuscripciones(conn, u.getIdUsuario());
            
            insertPerfiles(conn, u.getIdUsuario(), u.getPerfil());
            insertSuscripciones(conn, u.getIdUsuario(), u.getSuscripcion());
        }
    }

    /* --------------------------------------------------------------
       DELETE – elimina registro + relaciones N:N
       -------------------------------------------------------------- */
    public void delete(long idUsuario) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Eliminar relaciones N:N (tablas de unión)
            deletePerfiles(conn, idUsuario);
            deleteSuscripciones(conn, idUsuario);

            // 2. Eliminar la entidad principal
            String sql = "DELETE FROM Usuario WHERE idUsuario = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idUsuario);
                ps.executeUpdate();
            }
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga todo: empleado, perfiles, suscripciones
       -------------------------------------------------------------- */
    public Usuario findById(long idUsuario) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE idUsuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getLong("idUsuario"));
                    u.setContraseña(rs.getString("contraseña"));
                    u.setNombreUsuario(rs.getString("nombreUsuario"));

                    // Cargar objeto Empleado (1:1 o 1:N)
                    long idEmpleado = rs.getLong("idEmpleado");
                    Empleado empleado = empleadoDAO.findById(idEmpleado);
                    u.setEmpleado(empleado);

                    // Cargar lista de Perfiles (N:N)
                    List<Perfil> perfiles = findPerfilesByUsuario(conn, idUsuario);
                    u.setPerfil(perfiles);

                    // Cargar lista de Suscripciones (N:N)
                    // Usamos el método auxiliar de SuscripcionDAO para evitar recursión
                    List<Suscripcion> suscripciones = suscripcionDAO.findByUsuarioId(conn, idUsuario);
                    u.setSuscripcion(suscripciones);

                    return u;
                }
            }
        }
        return null; // No se encontró
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa con todas las relaciones
       -------------------------------------------------------------- */
    public List<Usuario> findAll() throws SQLException {
        String sql = "SELECT * FROM Usuario";
        List<Usuario> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                long idUsuario = rs.getLong("idUsuario");
                u.setIdUsuario(idUsuario);
                u.setContraseña(rs.getString("contraseña"));
                u.setNombreUsuario(rs.getString("nombreUsuario"));

                // Cargar Empleado
                long idEmpleado = rs.getLong("idEmpleado");
                Empleado empleado = empleadoDAO.findById(idEmpleado);
                u.setEmpleado(empleado);

                // Cargar Perfiles
                List<Perfil> perfiles = findPerfilesByUsuario(conn, idUsuario);
                u.setPerfil(perfiles);

                // Cargar Suscripciones
                List<Suscripcion> suscripciones = suscripcionDAO.findByUsuarioId(conn, idUsuario);
                u.setSuscripcion(suscripciones);

                list.add(u);
            }
        }
        return list;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES PARA RELACIÓN N:N CON Perfil
    // ==============================================================

    private void insertPerfiles(Connection conn, long idUsuario, List<Perfil> perfiles) throws SQLException {
        String sql = "INSERT INTO Usuario_Perfil (idUsuario, idPerfil) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Perfil p : perfiles) {
                ps.setLong(1, idUsuario);
                ps.setLong(2, p.getIdPerfil());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deletePerfiles(Connection conn, long idUsuario) throws SQLException {
        String sql = "DELETE FROM Usuario_Perfil WHERE idUsuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            ps.executeUpdate();
        }
    }

    private List<Perfil> findPerfilesByUsuario(Connection conn, long idUsuario) throws SQLException {
        String sql = "SELECT p.* FROM Perfil p " +
                     "JOIN Usuario_Perfil up ON p.idPerfil = up.idPerfil " +
                     "WHERE up.idUsuario = ?";
        List<Perfil> perfiles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Usamos findById del PerfilDAO para asegurar que el objeto esté completamente cargado.
                    long idPerfil = rs.getLong("idPerfil");
                    Perfil p = perfilDAO.findById(idPerfil);
                    if (p != null) {
                        perfiles.add(p);
                    }
                }
            }
        }
        return perfiles;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES PARA RELACIÓN N:N CON Suscripcion
    // ==============================================================

    private void insertSuscripciones(Connection conn, long idUsuario, List<Suscripcion> suscripciones) throws SQLException {
        String sql = "INSERT INTO Usuario_Suscripcion (idUsuario, idSuscripcion) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Suscripcion s : suscripciones) {
                ps.setLong(1, idUsuario);
                ps.setLong(2, s.getIdSuscripcion());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteSuscripciones(Connection conn, long idUsuario) throws SQLException {
        String sql = "DELETE FROM Usuario_Suscripcion WHERE idUsuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            ps.executeUpdate();
        }
    }
}