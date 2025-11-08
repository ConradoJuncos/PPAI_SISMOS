package com.ppai.app.controlador;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.google.gson.Gson;
import com.ppai.app.datos.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST principal para la aplicación Red Sísmica.
 * Contiene endpoints de ejemplo y el primer endpoint funcional para Eventos Sísmicos.
 */
public class ControladorPrincipal {

    private final Gson gson;
    // TODO: Agregar aquí tu Gestor u otras dependencias (Ej: private GestorSismos gestor;)

    public ControladorPrincipal() {
        this.gson = new Gson();
        // TODO: Inicializar tu gestor
    }

    /**
     * Objeto de transferencia de datos (DTO) simple para EventoSismico.
     */
    private record EventoSismicoDTO(
        long id,
        String fechaOcurrencia,
        String latitudEpicentro,
        String longitudEpicentro,
        double magnitud
    ) {}

    /**
     * Registra las rutas/endpoints de este controlador
     */
    public void registrarRutas(Javalin app) {
        // Endpoints de ejemplo (generales)
        app.get("/api/ejemplo", this::obtenerEjemplo);
        app.post("/api/ejemplo", this::crearEjemplo);
        
        // Endpoints funcionales del sistema
        // Prueba con: GET http://localhost:8080/api/eventos_sismicos
        app.get("/api/eventos_sismicos", this::obtenerEventosSismicos);
    }

    /* --------------------------------------------------------------
       Endpoints de Ejemplo
       -------------------------------------------------------------- */

    private void obtenerEjemplo(Context ctx) {
        ctx.json(java.util.Map.of(
            "mensaje", "Este es un endpoint de ejemplo (GET)",
            "info", "Agrega aquí tu lógica de negocio",
            "metodo", "GET"
        ));
    }

    private void crearEjemplo(Context ctx) {
        ctx.status(201).json(java.util.Map.of(
            "mensaje", "Recurso de ejemplo creado exitosamente (POST)",
            "id", 1,
            "metodo", "POST"
        ));
    }

    // --------------------------------------------------------------------------------

    /* --------------------------------------------------------------
       Endpoints Funcionales
       -------------------------------------------------------------- */

    /**
     * Endpoint GET "/api/eventos_sismicos"
     * Obtiene una lista de Eventos Sísmicos de la base de datos.
     * * NOTA: Este método hace la consulta directa a la BD.
     * En una arquitectura real, debería llamar a un Gestor/Servicio.
     */
    private void obtenerEventosSismicos(Context ctx) {
        // Seleccionamos los campos principales para el DTO
        String sql = """
            SELECT 
                idEventoSismico, 
                fechaHoraOcurrencia, 
                latitudEpicentro, 
                longitudEpicentro, 
                valorMagnitud 
            FROM EventoSismico 
            ORDER BY fechaHoraOcurrencia DESC
            LIMIT 10
            """;
        
        List<EventoSismicoDTO> eventos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                EventoSismicoDTO evento = new EventoSismicoDTO(
                    rs.getLong("idEventoSismico"),
                    rs.getString("fechaHoraOcurrencia"),
                    rs.getString("latitudEpicentro"),
                    rs.getString("longitudEpicentro"),
                    rs.getDouble("valorMagnitud")
                );
                eventos.add(evento);
            }

            // Si la tabla está vacía, mostramos un mensaje para la prueba
            if (eventos.isEmpty()) {
                ctx.json(Map.of(
                    "mensaje", "No hay Eventos Sísmicos registrados. Necesitas poblar la base de datos.",
                    "instruccion", "El Generador de Datos Sintéticos es el siguiente paso."
                ));
                return;
            }

            ctx.json(Map.of(
                "cantidad", eventos.size(),
                "eventos", eventos
            ));
        } catch (SQLException e) {
            ctx.status(500).json(Map.of("error", "Error de base de datos al obtener eventos: " + e.getMessage()));
            e.printStackTrace();
        }
    }
}