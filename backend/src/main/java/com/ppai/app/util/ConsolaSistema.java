package com.ppai.app.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsolaSistema {

    // ====== Colores ANSI ======
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String RED = "\u001B[31m";
    private static final String GRAY = "\u001B[90m";
    private static final String WHITE_BOLD = "\u001B[1;37m";

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static String hora() {
        return GRAY + "[" + LocalDateTime.now().format(TIME_FMT) + "]" + RESET;
    }

    // ====== Bloques generales ======
    public static void titulo(String texto) {
        System.out.println("\n" + CYAN + "══════════════════════════════════════════════════════════════════════");
        System.out.println("> " + texto.toUpperCase());
        System.out.println("══════════════════════════════════════════════════════════════════════" + RESET);
    }

    private static void bloque(String numero, String descripcion, String color) {
        System.out.println("\n" + color + "──────────────────────────────────────────────────────────────" + RESET);
        System.out.println(color + "[" + numero + "] " + descripcion + RESET);
    }

    private static void finBloque(String color) {
        System.out.println(color + "──────────────────────────────────────────────────────────────" + RESET);
    }

    // ====== Logs ======
    public static void info(String mensaje) {
        System.out.println(hora() + " " + CYAN + "[INFO]" + RESET + " " + mensaje);
    }

    public static void ok(String mensaje) {
        System.out.println(hora() + " " + GREEN + "[OK]" + RESET + " " + mensaje);
    }

    public static void advertencia(String mensaje) {
        System.out.println(hora() + " " + YELLOW + "[WARN]" + RESET + " " + mensaje);
    }

    public static void error(String mensaje) {
        System.out.println(hora() + " " + RED + "[ERROR]" + RESET + " " + mensaje);
    }

    public static void detalle(String etiqueta, Object valor) {
        System.out.println(GRAY + "       - " + etiqueta + ": " + valor + RESET);
    }

    // ====== Secciones predefinidas ======
    public static void iniciarBaseDeDatos(Runnable tarea) {
        bloque("1", "Inicializando Base de Datos", YELLOW);
        info("Conectando y creando tablas...");
        tarea.run();
        ok("Base de Datos inicializada correctamente");
        finBloque(YELLOW);
    }

    public static void iniciarContexto(Runnable tarea, int eventos, int usuarios, int sismografos) {
        bloque("2", "Cargando Contexto de Dominio", MAGENTA);
        tarea.run();
        ok("Contexto cargado con éxito");
        detalle("Eventos Sísmicos", eventos);
        detalle("Usuarios", usuarios);
        detalle("Sismógrafos", sismografos);
        finBloque(MAGENTA);
    }

    public static void crearServidor(Runnable tarea) {
        bloque("3", "Creando Servidor Javalin", BLUE);
        tarea.run();
        ok("Servidor Javalin configurado correctamente");
        finBloque(BLUE);
    }

    public static void registrarControladores(Runnable tarea) {
        bloque("4", "Registrando Controladores", CYAN);
        tarea.run();
        ok("Controlador de Revisión Manual registrado");
        finBloque(CYAN);
    }

    public static void registrarEndpoints(Runnable tarea) {
        bloque("5", "Registrando Endpoints Básicos", GREEN);
        tarea.run();
        ok("Endpoints de salud y prueba listos");
        finBloque(GREEN);
    }

    public static void iniciarServidor(Runnable tarea, int puerto) {
        bloque("6", "Iniciando Servidor...", YELLOW);
        tarea.run();
        ok("Servidor iniciado correctamente en: " + WHITE_BOLD + "http://localhost:" + puerto + RESET);
        info("Endpoint principal de revisión manual:");
        info("curl -X GET http://localhost:" + puerto + "/api/registrarResultadoRevisionManual?usuario=Emanuel");
        finBloque(YELLOW);
    }

    public static void cerrarServidor(Runnable tarea) {
        bloque("x", "Cerrando servidor...", BLUE);
        tarea.run();
        ok("Servidor cerrado correctamente");
        finBloque(BLUE);
    }
}