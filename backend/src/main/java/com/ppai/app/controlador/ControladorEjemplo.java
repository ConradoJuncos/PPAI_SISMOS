package com.ppai.app.controlador;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.google.gson.Gson;
import com.ppai.app.entidad.EntidadEjemplo;
import com.ppai.app.datos.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST de ejemplo.
 * Define aquí tus endpoints y la lógica para manejar las peticiones HTTP.
 */
public class ControladorEjemplo {

    private final Gson gson;
    // TODO: Agregar aquí tu Gestor u otras dependencias
    // Ejemplo: private Gestor gestor;

    public ControladorEjemplo() {
        this.gson = new Gson();
        // TODO: Inicializar tu gestor según el patrón que uses
        // Ejemplo: this.gestor = new Gestor();
    }

    /**
     * Registra las rutas/endpoints de este controlador
     */
    public void registrarRutas(Javalin app) {
        // Endpoints de ejemplo
        app.get("/api/ejemplo", this::obtenerEjemplo);
        app.post("/api/ejemplo", this::crearEjemplo);

        // Endpoints para EntidadEjemplo
        app.post("/crear_entidad", this::crearEntidad);
        app.get("/obtener_entidades", this::obtenerEntidades);
        app.get("/obtener_entidad/{id}", this::obtenerEntidadPorId);

        // TODO: Agrega aquí tus propios endpoints
        // app.get("/api/entidades", this::obtenerTodas);
        // app.get("/api/entidades/{id}", this::obtenerPorId);
        // app.post("/api/entidades", this::crear);
        // app.put("/api/entidades/{id}", this::actualizar);
        // app.delete("/api/entidades/{id}", this::eliminar);
    }

    /**
     * Endpoint GET de ejemplo
     * Prueba con: GET http://localhost:8080/api/ejemplo
     */
    private void obtenerEjemplo(Context ctx) {
        ctx.json(java.util.Map.of(
            "mensaje", "Este es un endpoint de ejemplo",
            "info", "Agrega aquí tu lógica de negocio",
            "metodo", "GET"
        ));
    }

    /**
     * Endpoint POST de ejemplo
     * Prueba con: POST http://localhost:8080/api/ejemplo
     * Body (JSON): { "dato": "valor" }
     */
    private void crearEjemplo(Context ctx) {
        // Puedes parsear el body de la petición así:
        // MiEntidad entidad = gson.fromJson(ctx.body(), MiEntidad.class);

        ctx.status(201).json(java.util.Map.of(
            "mensaje", "Recurso creado exitosamente",
            "id", 1,
            "metodo", "POST"
        ));
    }

    /**
     * Endpoint POST "/crear_entidad"
     * Crea una nueva EntidadEjemplo en la base de datos SQLite
     * Prueba con: POST http://localhost:8080/crear_entidad
     * Body (JSON): { "nombre": "Mi Entidad" }
     */
    private void crearEntidad(Context ctx) {
        try {
            EntidadEjemplo entidad = gson.fromJson(ctx.body(), EntidadEjemplo.class);

            if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
                ctx.status(400).json(Map.of("error", "El nombre es requerido"));
                return;
            }

            String sql = "INSERT INTO entidad_ejemplo (nombre) VALUES (?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, entidad.getNombre());
                pstmt.executeUpdate();

                // SQLite: obtener el último ID insertado usando last_insert_rowid()
                String sqlLastId = "SELECT last_insert_rowid() as id";
                try (PreparedStatement pstmtLastId = conn.prepareStatement(sqlLastId);
                     ResultSet rs = pstmtLastId.executeQuery()) {

                    if (rs.next()) {
                        long id = rs.getLong("id");
                        entidad.setId(id);
                        ctx.status(201).json(Map.of(
                            "mensaje", "Entidad creada exitosamente",
                            "entidad", entidad
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            ctx.status(500).json(Map.of("error", "Error en la base de datos: " + e.getMessage()));
            e.printStackTrace();
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Error al procesar la solicitud: " + e.getMessage()));
        }
    }

    /**
     * Endpoint GET "/obtener_entidades"
     * Obtiene todas las entidades de la base de datos
     */
    private void obtenerEntidades(Context ctx) {
        try {
            List<EntidadEjemplo> entidades = new ArrayList<>();
            String sql = "SELECT id, nombre FROM entidad_ejemplo ORDER BY id DESC";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        EntidadEjemplo entidad = new EntidadEjemplo();
                        entidad.setId(rs.getLong("id"));
                        entidad.setNombre(rs.getString("nombre"));
                        entidades.add(entidad);
                    }
                }
            }

            ctx.json(Map.of(
                "cantidad", entidades.size(),
                "entidades", entidades
            ));
        } catch (SQLException e) {
            ctx.status(500).json(Map.of("error", "Error en la base de datos: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * Endpoint GET "/obtener_entidad/:id"
     * Obtiene una entidad específica por ID
     */
    private void obtenerEntidadPorId(Context ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            String sql = "SELECT id, nombre FROM entidad_ejemplo WHERE id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setLong(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        EntidadEjemplo entidad = new EntidadEjemplo();
                        entidad.setId(rs.getLong("id"));
                        entidad.setNombre(rs.getString("nombre"));
                        ctx.json(entidad);
                    } else {
                        ctx.status(404).json(Map.of("error", "Entidad no encontrada"));
                    }
                }
            }
        } catch (SQLException e) {
            ctx.status(500).json(Map.of("error", "Error en la base de datos: " + e.getMessage()));
            e.printStackTrace();
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "ID inválido"));
        }
    }
}

