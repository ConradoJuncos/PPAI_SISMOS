package com.ppai.app;

import com.ppai.app.controlador.ControladorEjemplo;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

/**
 * Clase principal que inicia el servidor HTTP del backend.
 * Aquí puedes configurar tu aplicación según el patrón arquitectónico que necesites implementar.
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

        // Registrar el controlador de ejemplo (puedes comentar o eliminar después)
        ControladorEjemplo controladorEjemplo = new ControladorEjemplo();
        controladorEjemplo.registrarRutas(app);

        // TODO: Registrar aquí tus controladores y rutas
        // Ejemplo:
        // MiControlador controlador = new MiControlador();
        // controlador.registrarRutas(app);

        // Ruta de health check
        app.get("/", ctx -> ctx.result("Backend funcionando correctamente"));

        app.get("/health", ctx -> ctx.json(java.util.Map.of(
                "status", "UP",
                "timestamp", java.time.LocalDateTime.now().toString()
        )));

        // Iniciar el servidor
        app.start(PORT);

        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║   Backend - Servidor Iniciado                         ║");
        System.out.println("║   Puerto: " + PORT + "                                        ║");
        System.out.println("║   URL: http://localhost:" + PORT + "                          ║");
        System.out.println("║   CORS: Habilitado                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println("\nPuedes probar la API en http://localhost:" + PORT);
        System.out.println("Presiona Ctrl+C para detener el servidor\n");

        // Hook para cerrar recursos al detener el servidor
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nCerrando servidor...");
            app.stop();
            // TODO: Cerrar aquí tus conexiones de base de datos si las usas
            System.out.println("Servidor cerrado correctamente");
        }));
    }
}

