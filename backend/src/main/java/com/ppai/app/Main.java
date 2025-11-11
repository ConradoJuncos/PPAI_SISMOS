package com.ppai.app;

import com.ppai.app.controlador.ControladorRevisionManual;
import com.ppai.app.contexto.Contexto;
import com.ppai.app.datos.DatabaseConnection;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class Main {

    private static final int PORT = 8080;

    // ====== Códigos ANSI de color ======
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String WHITE_BOLD = "\u001B[1;37m";
    private static final String GRAY = "\u001B[90m";

    public static void main(String[] args) {

        System.out.println("\n" + CYAN +
                "══════════════════════════════════════════════════════════════════════");
        System.out.println("> INICIALIZACIÓN DEL SISTEMA RED SÍSMICA");
        System.out.println("══════════════════════════════════════════════════════════════════════" + RESET);

        // ============================================================
        // 1. Inicialización de la Base de Datos
        // ============================================================
        System.out.println("\n" + YELLOW + "──────────────────────────────────────────────────────────────" + RESET);
        System.out.println(YELLOW + "[1] Inicializando Base de Datos" + RESET);
        System.out.println(GRAY + "Inicializando Base de Datos..." + RESET);

        DatabaseConnection.initDatabase();
        System.out.println(GREEN + "   [+] Base de Datos inicializada correctamente" + RESET);
        System.out.println(YELLOW + "──────────────────────────────────────────────────────────────" + RESET);

        // ============================================================
        // 2. Inicialización del Contexto de dominio
        // ============================================================
        System.out.println("\n" + MAGENTA + "──────────────────────────────────────────────────────────────" + RESET);
        System.out.println(MAGENTA + "[2] Cargando Contexto de Dominio" + RESET);
        System.out.println(GRAY + "Inicializando contexto..." + RESET);

        Contexto contexto = new Contexto();
        System.out.println(GREEN + "   [+] Contexto cargado con éxito:" + RESET);
        System.out.println(GREEN + "       - Eventos Sísmicos:  " + contexto.getEventosSismicos().size());
        System.out.println(GREEN + "       - Usuarios:          " + contexto.getUsuarios().size());
        System.out.println(GREEN + "       - Sismógrafos:       " + contexto.getSismografos().size() + RESET);
        System.out.println(MAGENTA + "──────────────────────────────────────────────────────────────" + RESET);

        // ============================================================
        // 3. Crear el servidor Javalin
        // ============================================================
        System.out.println("\n" + BLUE + "──────────────────────────────────────────────────────────────" + RESET);
        System.out.println(BLUE + "[3] Creando Servidor Javalin" + RESET);

        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));
            config.http.asyncTimeout = 10_000L;
        });

        System.out.println(GREEN + "   [+] Servidor Javalin configurado correctamente" + RESET);
        System.out.println(BLUE + "──────────────────────────────────────────────────────────────" + RESET);

        // ============================================================
        // 4. Registrar controladores
        // ============================================================
        System.out.println("\n" + CYAN + "──────────────────────────────────────────────────────────────" + RESET);
        System.out.println(CYAN + "[4] Registrando Controladores" + RESET);

        ControladorRevisionManual controladorRevisionManual = new ControladorRevisionManual(contexto);
        controladorRevisionManual.registrarRutas(app);

        System.out.println(GREEN + "   [+] Controlador de Revisión Manual registrado" + RESET);
        System.out.println(CYAN + "──────────────────────────────────────────────────────────────" + RESET);

        // ============================================================
        // 5. Endpoints básicos
        // ============================================================
        System.out.println("\n" + MAGENTA + "──────────────────────────────────────────────────────────────" + RESET);
        System.out.println(MAGENTA + "[5] Registrando Endpoints Básicos" + RESET);

        app.get("/", ctx -> ctx.result("Backend funcionando correctamente"));
        app.get("/health", ctx -> ctx.json(java.util.Map.of(
                "status", "UP",
                "timestamp", java.time.LocalDateTime.now().toString()
        )));

        System.out.println(GREEN + "   [+] Endpoints de salud y prueba listos" + RESET);
        System.out.println(MAGENTA + "──────────────────────────────────────────────────────────────" + RESET);

        // ============================================================
        // 6. Iniciar servidor
        // ============================================================
        System.out.println("\n" + YELLOW + "──────────────────────────────────────────────────────────────" + RESET);
        System.out.println(YELLOW + "[6] Iniciando Servidor..." + RESET);

        app.start(PORT);

        System.out.println(GREEN + "   [+] Servidor iniciado correctamente en: " + WHITE_BOLD + "http://localhost:" + PORT + RESET);
        System.out.println(GRAY + "       Endpoint principal de revisión manual:" + RESET);
        System.out.println(GRAY + "          GET /api/registrarResultadoRevisionManual?usuario=analista1" + RESET);
        System.out.println(YELLOW + "──────────────────────────────────────────────────────────────" + RESET);

        // ============================================================
        // 7. Cierre controlado
        // ============================================================
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n" + BLUE + "──────────────────────────────────────────────────────────────" + RESET);
            System.out.println(BLUE + "[x] Cerrando servidor..." + RESET);
            app.stop();
            System.out.println(GREEN + "   [+] Servidor cerrado correctamente" + RESET);
            System.out.println(BLUE + "──────────────────────────────────────────────────────────────" + RESET);
        }));
    }
}
