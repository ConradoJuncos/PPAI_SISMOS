package com.ppai.app.dao;

import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PlanConstruccionESDAO {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final SismografoDAO sismografoDAO = new SismografoDAO();
    private final EstacionSismologicaDAO estacionDAO = new EstacionSismologicaDAO();
    private final TrabajoARealizarDAO trabajoDAO = new TrabajoARealizarDAO(); // ← Asumimos que existe

    /* --------------------------------------------------------------
       INSERT – guarda datos principales + relaciones
       -------------------------------------------------------------- */
    public void insert(PlanConstruccionES p) throws SQLException {
        String sql = """
            INSERT INTO PlanConstruccionES 
            (fechaPrevistaInicio, fechaProbableInicioPruebas, fechaFinalizacion,
             idEncargadoInstalacion, identificadorSismografo, codigoEstacion)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, p.getFechaPrevistaInicio());
            ps.setObject(2, p.getFechaProbableInicioPruebas());
            ps.setObject(3, p.getFechaFinalizacion());
            ps.setObject(4, p.getEncargadoInstalacion() != null ? p.getEncargadoInstalacion().getIdEmpleado() : null);
            ps.setObject(5, p.getSismografoAsignado() != null ? p.getSismografoAsignado().getIdentificadorSismografo() : null);
            ps.setLong  (6, p.getEstacionSismologica().getCodigoEstacion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long codigoPlan = rs.getLong(1);
                    p.setcodigoPlanConstruccion(codigoPlan);

                    // Persistir relación 1:N con trabajos
                    insertTrabajos(conn, codigoPlan, p.getTrabajosARealizar());
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza todo (incluyendo relaciones)
       -------------------------------------------------------------- */
    public void update(PlanConstruccionES p) throws SQLException {
        String sql = """
            UPDATE PlanConstruccionES SET 
            fechaPrevistaInicio = ?, fechaProbableInicioPruebas = ?, fechaFinalizacion = ?,
            idEncargadoInstalacion = ?, identificadorSismografo = ?, codigoEstacion = ?
            WHERE codigoPlanConstruccion = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, p.getFechaPrevistaInicio());
            ps.setObject(2, p.getFechaProbableInicioPruebas());
            ps.setObject(3, p.getFechaFinalizacion());
            ps.setObject(4, p.getEncargadoInstalacion() != null ? p.getEncargadoInstalacion().getIdEmpleado() : null);
            ps.setObject(5, p.getSismografoAsignado() != null ? p.getSismografoAsignado().getIdentificadorSismografo() : null);
            ps.setLong  (6, p.getEstacionSismologica().getCodigoEstacion());
            ps.setLong  (7, p.getcodigoPlanConstruccion());

            ps.executeUpdate();

            // Actualizar relación 1:N
            deleteTrabajos(conn, p.getcodigoPlanConstruccion());
            insertTrabajos(conn, p.getcodigoPlanConstruccion(), p.getTrabajosARealizar());
        }
    }

    /* --------------------------------------------------------------
       DELETE – elimina plan + relaciones
       -------------------------------------------------------------- */
    public void delete(long codigoPlanConstruccion) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            deleteTrabajos(conn, codigoPlanConstruccion);

            String sql = "DELETE FROM PlanConstruccionES WHERE codigoPlanConstruccion = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, codigoPlanConstruccion);
                ps.executeUpdate();
            }
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga todo: empleado, sismógrafo, estación, trabajos
       -------------------------------------------------------------- */
    public PlanConstruccionES findById(long codigoPlanConstruccion) throws SQLException {
        String sql = "SELECT * FROM PlanConstruccionES WHERE codigoPlanConstruccion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, codigoPlanConstruccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PlanConstruccionES p = new PlanConstruccionES();

                    p.setcodigoPlanConstruccion(rs.getLong("codigoPlanConstruccion"));
                    p.setFechaPrevistaInicio(getLocalDateTime(rs, "fechaPrevistaInicio"));
                    p.setFechaProbableInicioPruebas(getLocalDateTime(rs, "fechaProbableInicioPruebas"));
                    p.setFechaFinalizacion(getLocalDateTime(rs, "fechaFinalizacion"));

                    // Cargar objetos relacionados
                    Long idEmpleado = rs.getObject("idEncargadoInstalacion", Long.class);
                    p.setEncargadoInstalacion(idEmpleado != null ? empleadoDAO.findById(idEmpleado) : null);

                    Long idSismografo = rs.getObject("identificadorSismografo", Long.class);
                    p.setSismografoAsignado(idSismografo != null ? sismografoDAO.findById(idSismografo) : null);

                    long codigoEstacion = rs.getLong("codigoEstacion");
                    p.setEstacionSismologica(estacionDAO.findById(codigoEstacion));

                    // Cargar trabajos
                    List<TrabajoARealizar> trabajos = trabajoDAO.findByPlanConstruccionId(conn, codigoPlanConstruccion);
                    p.setTrabajosARealizar(trabajos);

                    return p;
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       FIND ALL – lista completa con todas las relaciones
       -------------------------------------------------------------- */
    public List<PlanConstruccionES> findAll() throws SQLException {
        String sql = "SELECT * FROM PlanConstruccionES";
        List<PlanConstruccionES> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PlanConstruccionES p = new PlanConstruccionES();

                p.setcodigoPlanConstruccion(rs.getLong("codigoPlanConstruccion"));
                p.setFechaPrevistaInicio(getLocalDateTime(rs, "fechaPrevistaInicio"));
                p.setFechaProbableInicioPruebas(getLocalDateTime(rs, "fechaProbableInicioPruebas"));
                p.setFechaFinalizacion(getLocalDateTime(rs, "fechaFinalizacion"));

                Long idEmpleado = rs.getObject("idEncargadoInstalacion", Long.class);
                p.setEncargadoInstalacion(idEmpleado != null ? empleadoDAO.findById(idEmpleado) : null);

                Long idSismografo = rs.getObject("identificadorSismografo", Long.class);
                p.setSismografoAsignado(idSismografo != null ? sismografoDAO.findById(idSismografo) : null);

                long codigoEstacion = rs.getLong("codigoEstacion");
                p.setEstacionSismologica(estacionDAO.findById(codigoEstacion));

                long codigoPlan = p.getcodigoPlanConstruccion();
                List<TrabajoARealizar> trabajos = trabajoDAO.findByPlanConstruccionId(conn, codigoPlan);
                p.setTrabajosARealizar(trabajos);

                list.add(p);
            }
        }
        return list;
    }

    // ==============================================================
    // MÉTODOS AUXILIARES
    // ==============================================================

    private void insertTrabajos(Connection conn, long codigoPlan, List<TrabajoARealizar> trabajos) throws SQLException {
        String sql = "INSERT INTO PlanConstruccionES_Trabajo (codigoPlanConstruccion, idTrabajo) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (TrabajoARealizar t : trabajos) {
                ps.setLong(1, codigoPlan);
                ps.setLong(2, t.getIdTrabajoARealizar());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteTrabajos(Connection conn, long codigoPlan) throws SQLException {
        String sql = "DELETE FROM PlanConstruccionES_Trabajo WHERE codigoPlanConstruccion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, codigoPlan);
            ps.executeUpdate();
        }
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}