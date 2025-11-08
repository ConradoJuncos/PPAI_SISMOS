package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TrabajoARealizarDAO {

    private final TipoTrabajoDAO tipoTrabajoDAO = new TipoTrabajoDAO();

    /* --------------------------------------------------------------
       INSERT – guarda todos los campos + relación con TipoTrabajo
       -------------------------------------------------------------- */
    public void insert(TrabajoARealizar t) throws SQLException {
        String sql = """
            INSERT INTO TrabajoARealizar 
            (comentario, fechaInicioPrevista, fechaInicioReal, fechaFinPrevista, fechaFinReal, idTipoTrabajo)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getComentario());
            ps.setObject(2, t.getFechaInicioPrevista());
            ps.setObject(3, t.getFechaInicioReal());
            ps.setObject(4, t.getFechaFinPrevista());
            ps.setObject(5, t.getFechaFinReal());
            ps.setLong  (6, t.getDefinicionTrabajo().getIdTipoTrabajo());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setIdTrabajoARealizar(rs.getLong(1));
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todo
       -------------------------------------------------------------- */
    public void update(TrabajoARealizar t) throws SQLException {
        String sql = """
            UPDATE TrabajoARealizar SET 
            comentario = ?, fechaInicioPrevista = ?, fechaInicioReal = ?, 
            fechaFinPrevista = ?, fechaFinReal = ?, idTipoTrabajo = ?
            WHERE idTrabajoARealizar = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getComentario());
            ps.setObject(2, t.getFechaInicioPrevista());
            ps.setObject(3, t.getFechaInicioReal());
            ps.setObject(4, t.getFechaFinPrevista());
            ps.setObject(5, t.getFechaFinReal());
            ps.setLong  (6, t.getDefinicionTrabajo().getIdTipoTrabajo());
            ps.setLong  (7, t.getIdTrabajoARealizar());

            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       DELETE – por PK
       -------------------------------------------------------------- */
    public void delete(long idTrabajoARealizar) throws SQLException {
        String sql = "DELETE FROM TrabajoARealizar WHERE idTrabajoARealizar = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTrabajoARealizar);
            ps.executeUpdate();
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga completo con TipoTrabajo
       -------------------------------------------------------------- */
    public TrabajoARealizar findById(long idTrabajoARealizar) throws SQLException {
        String sql = "SELECT * FROM TrabajoARealizar WHERE idTrabajoARealizar = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idTrabajoARealizar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TrabajoARealizar t = new TrabajoARealizar();

                    t.setIdTrabajoARealizar(rs.getLong("idTrabajoARealizar"));
                    t.setComentario(rs.getString("comentario"));
                    t.setFechaInicioPrevista(getLocalDateTime(rs, "fechaInicioPrevista"));
                    t.setFechaInicioReal(getLocalDateTime(rs, "fechaInicioReal"));
                    t.setFechaFinPrevista(getLocalDateTime(rs, "fechaFinPrevista"));
                    t.setFechaFinReal(getLocalDateTime(rs, "fechaFinReal"));

                    long idTipo = rs.getLong("idTipoTrabajo");
                    t.setDefinicionTrabajo(tipoTrabajoDAO.findById(idTipo));

                    return t;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL
       -------------------------------------------------------------- */
    public List<TrabajoARealizar> findAll() throws SQLException {
        String sql = "SELECT * FROM TrabajoARealizar";
        List<TrabajoARealizar> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TrabajoARealizar t = new TrabajoARealizar();

                t.setIdTrabajoARealizar(rs.getLong("idTrabajoARealizar"));
                t.setComentario(rs.getString("comentario"));
                t.setFechaInicioPrevista(getLocalDateTime(rs, "fechaInicioPrevista"));
                t.setFechaInicioReal(getLocalDateTime(rs, "fechaInicioReal"));
                t.setFechaFinPrevista(getLocalDateTime(rs, "fechaFinPrevista"));
                t.setFechaFinReal(getLocalDateTime(rs, "fechaFinReal"));

                long idTipo = rs.getLong("idTipoTrabajo");
                t.setDefinicionTrabajo(tipoTrabajoDAO.findById(idTipo));

                list.add(t);
            }
        }
        return list;
    }

    /* --------------------------------------------------------------
       NUEVO: Buscar Trabajos por Plan de Construcción (para PlanConstruccionESDAO)
       -------------------------------------------------------------- */
    public List<TrabajoARealizar> findByPlanConstruccionId(Connection conn, long codigoPlanConstruccion) throws SQLException {
        String sql = """
            SELECT t.* FROM TrabajoARealizar t
            JOIN PlanConstruccionES_Trabajo pct ON t.idTrabajoARealizar = pct.idTrabajo
            WHERE pct.codigoPlanConstruccion = ?
            """;
        List<TrabajoARealizar> trabajos = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, codigoPlanConstruccion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TrabajoARealizar t = new TrabajoARealizar();

                    t.setIdTrabajoARealizar(rs.getLong("idTrabajoARealizar"));
                    t.setComentario(rs.getString("comentario"));
                    t.setFechaInicioPrevista(getLocalDateTime(rs, "fechaInicioPrevista"));
                    t.setFechaInicioReal(getLocalDateTime(rs, "fechaInicioReal"));
                    t.setFechaFinPrevista(getLocalDateTime(rs, "fechaFinPrevista"));
                    t.setFechaFinReal(getLocalDateTime(rs, "fechaFinReal"));

                    long idTipo = rs.getLong("idTipoTrabajo");
                    t.setDefinicionTrabajo(tipoTrabajoDAO.findById(idTipo));

                    trabajos.add(t);
                }
            }
        }
        return trabajos;
    }

    // ==============================================================
    // UTILIDAD: convertir Timestamp a LocalDateTime
    // ==============================================================
    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}