package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Perfil;
import com.ppai.app.entidad.Permiso;
import java.sql.*;
import java.util.*;

public class PerfilDAO {

    private final PermisoDAO permisoDAO = new PermisoDAO();

    /* --------------------------------------------------------------
       INSERT – guarda datos principales + relación N:N con permisos
       -------------------------------------------------------------- */
    public void insert(Perfil p) throws SQLException {
        String sql = "INSERT INTO Perfil (nombre, descripcion) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long idPerfil = rs.getLong(1);
                    p.setIdPerfil(idPerfil);

                    // Persistir relación N:N con permisos
                    insertPermisos(conn, idPerfil, p.getPermiso());
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todo (incluyendo permisos)
       -------------------------------------------------------------- */
    public void update(Perfil p) throws SQLException {
        String sql = "UPDATE Perfil SET nombre = ?, descripcion = ? WHERE idPerfil = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setLong  (3, p.getIdPerfil());

            ps.executeUpdate();

            // Actualizar relación N:N
            deletePermisos(conn, p.getIdPerfil());
            insertPermisos(conn, p.getIdPerfil(), p.getPermiso());
        }
    }

    /* --------------------------------------------------------------
       DELETE – elimina perfil + relaciones
       -------------------------------------------------------------- */
    public void delete(long idPerfil) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            deletePermisos(conn, idPerfil);

            String sql = "DELETE FROM Perfil WHERE idPerfil = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idPerfil);
                ps.executeUpdate();
            }
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga perfil + todos sus permisos
       -------------------------------------------------------------- */
    public Perfil findById(long idPerfil) throws SQLException {
        String sql = "SELECT * FROM Perfil WHERE idPerfil = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idPerfil);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Perfil p = new Perfil();

                    p.setIdPerfil(rs.getLong("idPerfil"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));

                    // Cargar permisos asociados
                    List<Permiso> permisos = findPermisosByPerfilId(conn, idPerfil);
                    p.setPermisos(permisos);

                    return p;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa con permisos cargados
       -------------------------------------------------------------- */
    public List<Perfil> findAll() throws SQLException {
        String sql = "SELECT * FROM Perfil";
        List<Perfil> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while ( rs.next() ) {
                Perfil p = new Perfil();

                p.setIdPerfil(rs.getLong("idPerfil"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));

                List<Permiso> permisos = findPermisosByPerfilId(conn, p.getIdPerfil());
                p.setPermisos(permisos);

                list.add(p);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       FIND BY NOMBRE – útil para búsquedas por nombre
       -------------------------------------------------------------- */
    public Perfil findByNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM Perfil WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Perfil p = new Perfil();
                    p.setIdPerfil(rs.getLong("idPerfil"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));

                    List<Permiso> permisos = findPermisosByPerfilId(conn, p.getIdPerfil());
                    p.setPermisos(permisos);

                    return p;
                }
            }
        }
        return null;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES PARA RELACIÓN N:N CON PERMISOS
    // ==============================================================

    private void insertPermisos(Connection conn, long idPerfil, List<Permiso> permisos) throws SQLException {
        String sql = "INSERT INTO Perfil_Permiso (idPerfil, idPermiso) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Permiso permiso : permisos) {
                ps.setLong(1, idPerfil);
                ps.setLong(2, permiso.getIdPermiso());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deletePermisos(Connection conn, long idPerfil) throws SQLException {
        String sql = "DELETE FROM Perfil_Permiso WHERE idPerfil = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPerfil);
            ps.executeUpdate();
        }
    }

    private List<Permiso> findPermisosByPerfilId(Connection conn, long idPerfil) throws SQLException {
        String sql = """
            SELECT p.* FROM Permiso p
            JOIN Perfil_Permiso pp ON p.idPermiso = pp.idPermiso
            WHERE pp.idPerfil = ?
            """;
        List<Permiso> permisos = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPerfil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Permiso permiso = new Permiso();
                    permiso.setIdPermiso(rs.getLong("idPermiso"));
                    permiso.setNombre(rs.getString("nombre"));
                    permiso.setDescripcion(rs.getString("descripcion"));
                    permisos.add(permiso);
                }
            }
        }
        return permisos;
    }
}