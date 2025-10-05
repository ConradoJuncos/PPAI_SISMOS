package com.ppai.app.controlador;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.google.gson.Gson;

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

    // TODO: Implementa aquí tus métodos handler para cada endpoint
    //
    // Ejemplo de endpoint que retorna una lista:
    // private void obtenerTodas(Context ctx) {
    //     List<Entidad> entidades = gestor.obtenerTodas();
    //     ctx.json(entidades);
    // }
    //
    // Ejemplo de endpoint con parámetro de ruta:
    // private void obtenerPorId(Context ctx) {
    //     Long id = Long.parseLong(ctx.pathParam("id"));
    //     Optional<Entidad> entidad = gestor.obtenerPorId(id);
    //
    //     if (entidad.isPresent()) {
    //         ctx.json(entidad.get());
    //     } else {
    //         ctx.status(404).json(Map.of("error", "No encontrado"));
    //     }
    // }
    //
    // Ejemplo de endpoint POST que recibe datos:
    // private void crear(Context ctx) {
    //     try {
    //         Entidad entidad = gson.fromJson(ctx.body(), Entidad.class);
    //         Entidad nuevaEntidad = gestor.crear(entidad);
    //         ctx.status(201).json(nuevaEntidad);
    //     } catch (Exception e) {
    //         ctx.status(400).json(Map.of("error", e.getMessage()));
    //     }
    // }
}

