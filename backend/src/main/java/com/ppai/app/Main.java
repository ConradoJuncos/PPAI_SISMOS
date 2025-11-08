package com.ppai.app;

import com.ppai.app.controlador.ControladorPrincipal; // Se actualiza la importación
import com.ppai.app.datos.DatabaseConnection;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

/**
 * Clase principal que inicia el servidor HTTP del backend.
 */
public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {

        // Crear el servidor Javalin
        Javalin app = Javalin.create(config -> {
            // Configurar CORS para permitir conexiones desde el frontend de escritorio
            config.plugins.enableCors(cors -> {
                cors.add(CorsPluginConfig::anyHost);
            });

            // Configurar logging
            config.http.asyncTimeout = 10_000L;
        });

        // Registrar el controlador principal del sistema
        ControladorPrincipal controladorPrincipal = new ControladorPrincipal(); // Se actualiza la clase
        controladorPrincipal.registrarRutas(app); // Se registran las rutas del nuevo controlador

        // Ruta de health check
        app.get("/", ctx -> ctx.result("Backend funcionando correctamente"));

        app.get("/health", ctx -> ctx.json(java.util.Map.of(
                "status", "UP",
                "timestamp", java.time.LocalDateTime.now().toString()
        )));

        // Iniciar el servidor
        app.start(PORT);

        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║   Backend - Servidor Iniciado                         ║");
        System.out.println("║   Puerto: " + PORT + "                                        ║");
        System.out.println("║   URL: http://localhost:" + PORT + "                          ║");
        System.out.println("║   CORS: Habilitado                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println("\nPuedes probar la API en http://localhost:" + PORT);
        System.out.println("Prueba el endpoint de ejemplo de sismos: http://localhost:" + PORT + "/api/eventos_sismicos");
        System.out.println("Presiona Ctrl+C para detener el servidor\n");

        // Hook para cerrar recursos al detener el servidor
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nCerrando servidor...");
            app.stop();
            DatabaseConnection.cerrarConexion();
            System.out.println("Servidor cerrado correctamente");
        }));
    }
}