package com.ppai.app.dao;

import com.ppai.app.entidad.OrdenDeInspeccion;
import com.ppai.app.entidad.EstacionSismologica;
import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.entidad.Empleado;
import com.ppai.app.entidad.Estado;
import com.ppai.app.entidad.TareaAsignada;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad OrdenDeInspeccion, corregido según la clase de entidad proporcionada.
 * Maneja las operaciones CRUD y sus relaciones con Empleado, Estado, TareaAsignada y EstacionSismologica.
 */
public class OrdenDeInspeccionDAO {

    private final EstacionSismologicaDAO estacionSismologicaDAO = new EstacionSismologicaDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final EstadoDAO estadoDAO = new EstadoDAO();
    private final TareaAsignadaDAO tareaAsignadaDAO = new TareaAsignadaDAO();
    // private final CambioEstadoDAO cambioEstadoDAO = new CambioEstadoDAO(); // Asumiendo que el historial se maneja aparte.
    
    // El nombre de la tabla de BD es 'OrdenDeInspeccion' y su PK es 'numeroOrden'
    private static final String TABLE_NAME = "OrdenDeInspeccion";
    private static final String PK_NAME = "numeroOrden";

    /* --------------------------------------------------------------
       INSERT – guarda datos principales + dependencias 1:N
       -------------------------------------------------------------- */
    public void insert(OrdenDeInspeccion o) throws SQLException {
        // SQL corregido para incluir todos los campos de la entidad
        String sql = "INSERT INTO " + TABLE_NAME + " (fechaHoraCierre, fechaHoraFinalizacion, fechaHoraInicio, observacionCierre, idEmpleado, idEstado, idEstacionSismologica) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Fechas y String
            ps.setObject(1, o.getFechaHoraCierre());
            ps.setObject(2, o.getFechaHoraFinalizacion());
            ps.setObject(3, o.getFechaHoraInicio());
            ps.setString(4, o.getObservacionCierre());
            
            // Claves foráneas (FKs)
            ps.setLong(5, o.getEmpleado().getIdEmpleado());
            // Asumiendo que Estado tiene un ID
            ps.setLong(6, o.getEstado().getIdEstado()); 
            
            // Usar getCodigoEstacion() en lugar de getIdEstacionSismologica()
            ps.setLong(7, o.getEstacionSismologica().getCodigoEstacion()); 

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    // La PK se llama numeroOrden
                    long numeroOrden = rs.getLong(1);
                    o.estNumeroOden(numeroOrden); 

                    // Persistir la colección 1:N TareaAsignada
                    if (o.getTareaAsignada() != null) {
                        for (TareaAsignada ta : o.getTareaAsignada()) {
                            // Asumimos que TareaAsignadaDAO tiene un método para persistir
                            tareaAsignadaDAO.insertForOrden(conn, numeroOrden, ta); 
                        }
                    }
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza datos principales
       -------------------------------------------------------------- */
    public void update(OrdenDeInspeccion o) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET fechaHoraCierre = ?, fechaHoraFinalizacion = ?, fechaHoraInicio = ?, observacionCierre = ?, idEmpleado = ?, idEstado = ?, idEstacionSismologica = ? WHERE " + PK_NAME + " = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, o.getFechaHoraCierre());
            ps.setObject(2, o.getFechaHoraFinalizacion());
            ps.setObject(3, o.getFechaHoraInicio());
            ps.setString(4, o.getObservacionCierre());
            ps.setLong(5, o.getEmpleado().getIdEmpleado());
            ps.setLong(6, o.getEstado().getIdEstado());
            
            // Usar getCodigoEstacion() en lugar de getIdEstacionSismologica()
            ps.setLong(7, o.getEstacionSismologica().getCodigoEstacion()); 
            ps.setLong(8, o.getNumeroOrden());

            ps.executeUpdate();
            
            // Nota: Se asume que la lista TareaAsignada se gestiona mediante TareaAsignadaDAO
        }
    }

    /* --------------------------------------------------------------
       FIND BY ID – carga todas las relaciones
       -------------------------------------------------------------- */
    public OrdenDeInspeccion findByNumeroOrden(long numeroOrden) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + PK_NAME + " = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, numeroOrden);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrden(rs, conn);
                }
            }
        }
        return null;
    }

    /* --------------------------------------------------------------
       MÉTODOS AUXILIARES
       -------------------------------------------------------------- */
    private OrdenDeInspeccion mapResultSetToOrden(ResultSet rs, Connection conn) throws SQLException {
        OrdenDeInspeccion o = new OrdenDeInspeccion();
        long numeroOrden = rs.getLong(PK_NAME);
        o.estNumeroOden(numeroOrden);
        
        // Mapeo de campos directos
        o.setFechaHoraCierre(getLocalDateTime(rs, "fechaHoraCierre"));
        o.setFechaHoraFinalizacion(getLocalDateTime(rs, "fechaHoraFinalizacion"));
        o.setFechaHoraInicio(getLocalDateTime(rs, "fechaHoraInicio"));
        o.setObservacionCierre(rs.getString("observacionCierre"));

        // Cargar Empleado (1:N)
        long idEmpleado = rs.getLong("idEmpleado");
        Empleado empleado = empleadoDAO.findById(idEmpleado);
        o.setEmpleado(empleado);
        
        // Cargar Estado Actual (1:N - el puntero directo)
        long idEstado = rs.getLong("idEstado");
        Estado estado = estadoDAO.findById(idEstado);
        // o.setEstado(estado); // Se asume que el objeto OrdenDeInspeccion tiene setEstado, aunque no está en el snippet
        
        // Cargar EstacionSismologica (1:N)
        long idEstacion = rs.getLong("idEstacionSismologica");
        EstacionSismologica es = estacionSismologicaDAO.findById(idEstacion);
        o.setEstacionSismologica(es);
        
        // Cargar Colección TareaAsignada (1:N)
        List<TareaAsignada> tareas = tareaAsignadaDAO.findByOrdenId(conn, numeroOrden);
        o.setTareaAsignada(tareas);
        
        return o;
    }
    
    /**
     * Convierte un campo de texto de la base de datos a LocalDateTime (maneja NULL).
     */
    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        String dateTimeStr = rs.getString(column);
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr) : null;
    }
}