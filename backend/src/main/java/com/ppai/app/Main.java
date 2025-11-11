package com.ppai.app;

import com.ppai.app.controlador.ControladorRevisionManual;
import com.ppai.app.contexto.Contexto;
import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.util.ConsolaSistema;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {

        ConsolaSistema.titulo("INICIALIZACIÓN DEL SISTEMA RED SÍSMICA");

        // 1. Base de Datos
        ConsolaSistema.iniciarBaseDeDatos(DatabaseConnection::initDatabase);

        // 2. Contexto de Dominio
        Contexto contexto = new Contexto();
        ConsolaSistema.iniciarContexto(() -> {},
                contexto.getEventosSismicos().size(),
                contexto.getUsuarios().size(),
                contexto.getSismografos().size());

        // 3. Servidor Javalin
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));
            config.http.asyncTimeout = 10_000L;});
        ConsolaSistema.crearServidor(() -> {});

        // 4. Controladores
        ControladorRevisionManual controlador = new ControladorRevisionManual(contexto);
        ConsolaSistema.registrarControladores(() -> controlador.registrarRutas(app));

        // 5. Endpoints
        ConsolaSistema.registrarEndpoints(() -> {
            app.get("/", ctx -> ctx.result("Backend funcionando correctamente"));
            app.get("/health", ctx -> ctx.json(java.util.Map.of(
                    "status", "UP",
                    "timestamp", java.time.LocalDateTime.now().toString())));});

        // 6. Inicio Servidor
        ConsolaSistema.iniciarServidor(() -> app.start(PORT), PORT);

        // 7. Cierre controlado
        Runtime.getRuntime().addShutdownHook(new Thread(() -> ConsolaSistema.cerrarServidor(app::stop)));
    }
}
