package com.ppai.app;

import com.ppai.app.controlador.ControladorRevisionManual;
import com.ppai.app.contexto.Contexto;
import com.ppai.app.datos.DatabaseConnection;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import java.sql.SQLException;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {

        // #################################################
        // 1. Inicialización de la Base de Datos
        // #################################################
        try {
            System.out.println("Inicializando Base de Datos...");
            DatabaseConnection.getConnection();
            System.out.println("Base de Datos inicializada correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ ERROR FATAL: No se pudo conectar/crear la base de datos.");
            e.printStackTrace();
            return;
        }

        // #################################################
        // 2. Inicialización del Contexto de dominio
        // #################################################
        System.out.println("Inicializando contexto de dominio...");
        Contexto contexto = new Contexto();
        System.out.println("Contexto cargado con éxito:");
        System.out.println(" - Eventos: " + contexto.getEventosSismicos().size());
        System.out.println(" - Usuarios: " + contexto.getUsuarios().size());
        System.out.println(" - Sismógrafos: " + contexto.getSismografos().size());

        // #################################################
        // 3. Crear el servidor Javalin
        // #################################################
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));
            config.http.asyncTimeout = 10_000L;
        });

        // #################################################
        // 4. Registrar controladores
        // #################################################
        ControladorRevisionManual controladorRevisionManual = new ControladorRevisionManual(contexto);
        controladorRevisionManual.registrarRutas(app);

        // #################################################
        // 5. Endpoints básicos de prueba
        // #################################################
        app.get("/", ctx -> ctx.result("Backend funcionando correctamente"));
        app.get("/health", ctx -> ctx.json(java.util.Map.of(
                "status", "UP",
                "timestamp", java.time.LocalDateTime.now().toString()
        )));

        // #################################################
        // 6. Iniciar servidor
        // #################################################
        app.start(PORT);
        System.out.println("\nServidor iniciado en http://localhost:" + PORT);
        System.out.println("Endpoint de revisión manual:");
        System.out.println("👉 GET /api/registrarResultadoRevisionManual?usuario=analista1");

        // #################################################
        // 7. Cierre controlado
        // #################################################
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nCerrando servidor...");
            app.stop();
            DatabaseConnection.cerrarConexion();
            System.out.println("Servidor cerrado correctamente.");
        }));
    }
}
