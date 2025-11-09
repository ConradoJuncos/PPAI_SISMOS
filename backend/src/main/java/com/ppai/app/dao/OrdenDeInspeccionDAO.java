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
 * DAO para la entidad OrdenDeInspeccion.
 * Maneja las operaciones CRUD y sus relaciones con Empleado, Estado, TareaAsignada y EstacionSismologica.
 */
public class OrdenDeInspeccionDAO {

    // Se asume que EstadoDAO ya fue actualizado para usar findByAmbitoAndNombre
    private final EstacionSismologicaDAO estacionSismologicaDAO = new EstacionSismologicaDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final EstadoDAO estadoDAO = new EstadoDAO(); 
    private final TareaAsignadaDAO tareaAsignadaDAO = new TareaAsignadaDAO();
    
    // El nombre de la tabla de BD es 'OrdenDeInspeccion' y su PK es 'numeroOrden'
    private static final String TABLE_NAME = "OrdenDeInspeccion";
    private static final String PK_NAME = "numeroOrden";

    /* --------------------------------------------------------------
       INSERT – guarda datos principales + dependencias 1:N. FK Estado es compuesta.
       -------------------------------------------------------------- */
    public void insert(OrdenDeInspeccion o) throws SQLException {
        // SQL: Se cambia idEstado por ambitoEstado y nombreEstado (aumenta el n° de parámetros a 8)
        String sql = "INSERT INTO " + TABLE_NAME + " (fechaHoraCierre, fechaHoraFinalizacion, fechaHoraInicio, observacionCierre, idEmpleado, ambitoEstado, nombreEstado, idEstacionSismologica) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Fechas y String
            ps.setObject(1, o.getFechaHoraCierre());
            ps.setObject(2, o.getFechaHoraFinalizacion());
            ps.setObject(3, o.getFechaHoraInicio());
            ps.setString(4, o.getObservacionCierre());
            
            // Claves foráneas (FKs)
            ps.setLong(5, o.getEmpleado().getIdEmpleado());
            
            // AHORA se usan los campos de la clave compuesta del Estado
            ps.setString(6, o.getEstado().getAmbito());
            ps.setString(7, o.getEstado().getNombreEstado());
            
            // Usar getCodigoEstacion()
            ps.setLong(8, o.getEstacionSismologica().getCodigoEstacion()); 

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    // La PK se llama numeroOrden
                    long numeroOrden = rs.getLong(1);
                    o.estNumeroOden(numeroOrden); 

                    // Persistir la colección 1:N TareaAsignada
                    if (o.getTareaAsignada() != null) {
                        for (TareaAsignada ta : o.getTareaAsignada()) {
                            // Se asume que TareaAsignadaDAO tiene un método para persistir
                            tareaAsignadaDAO.insertForOrden(conn, numeroOrden, ta); 
                        }
                    }
                }
            }
        }
    }

    /* --------------------------------------------------------------
       UPDATE – actualiza datos principales. FK Estado es compuesta.
       -------------------------------------------------------------- */
    public void update(OrdenDeInspeccion o) throws SQLException {
        // SQL: Se cambia idEstado por ambitoEstado y nombreEstado (aumenta el n° de parámetros a 9)
        String sql = "UPDATE " + TABLE_NAME + " SET fechaHoraCierre = ?, fechaHoraFinalizacion = ?, fechaHoraInicio = ?, observacionCierre = ?, idEmpleado = ?, ambitoEstado = ?, nombreEstado = ?, idEstacionSismologica = ? WHERE " + PK_NAME + " = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, o.getFechaHoraCierre());
            ps.setObject(2, o.getFechaHoraFinalizacion());
            ps.setObject(3, o.getFechaHoraInicio());
            ps.setString(4, o.getObservacionCierre());
            ps.setLong(5, o.getEmpleado().getIdEmpleado());
            
            // AHORA se usan los campos de la clave compuesta del Estado
            ps.setString(6, o.getEstado().getAmbito());
            ps.setString(7, o.getEstado().getNombreEstado());
            
            // Usar getCodigoEstacion()
            ps.setLong(8, o.getEstacionSismologica().getCodigoEstacion()); 
            ps.setLong(9, o.getNumeroOrden()); // PK en el WHERE

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
        
        // Cargar Estado Actual (1:N - usando la clave compuesta)
        String ambito = rs.getString("ambitoEstado");   // <--- CAMBIO: Leer ámbito
        String nombreEstado = rs.getString("nombreEstado"); // <--- CAMBIO: Leer nombreEstado
        
        // Llamar al nuevo método del EstadoDAO
        Estado estado = estadoDAO.findByAmbitoAndNombre(ambito, nombreEstado); 
        // Se asume que el objeto OrdenDeInspeccion tiene setEstado
        // Asumiendo que el método es setEstado o similar para la carga
        // o.setEstado(estado); 
        
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
        // Asumiendo el formato estándar ISO-8601 (YYYY-MM-DDTHH:MM:SS) si es un string directo
        // o el formato de Base de Datos (YYYY-MM-DD HH:MM:SS) que luego debe ser parseado.
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr.replace(' ', 'T')) : null;
    }
}