package com.ppai.app.controlador;

import com.ppai.app.contexto.Contexto;
import com.ppai.app.entidad.EventoSismico;
import com.ppai.app.entidad.Usuario;
import com.ppai.app.gestor.GestorRevisionManual;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para el caso de uso:
 * Registrar Resultado de Revisión Manual (CU-23)
 */
public class ControladorRevisionManual {

    private final Contexto contexto;

    public ControladorRevisionManual(Contexto contexto) {
        this.contexto = contexto;
    }

    /**
     * Registra las rutas de este controlador en Javalin.
     */
    public void registrarRutas(Javalin app) {
        app.get("/api/registrarResultadoRevisionManual", this::registrarResultadoRevisionManual);
    }

    /**
     * Endpoint: /api/registrarResultadoRevisionManual?usuario=nombreUsuario
     *
     * Crea el GestorRevisionManual para el usuario indicado y devuelve un mensaje de confirmación.
     */
    private void registrarResultadoRevisionManual(Context ctx) {
        String nombreUsuario = ctx.queryParam("usuario");

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            ctx.status(400).result("Debe especificar el parámetro ?usuario=nombreUsuario");
            return;
        }

        // Buscar al usuario logueado
        Optional<Usuario> usuarioOpt = contexto.getUsuarios().stream()
                .filter(u -> nombreUsuario.equalsIgnoreCase(u.getNombreUsuario()))
                .findFirst();

        if (usuarioOpt.isEmpty()) {
            ctx.status(404).result("No se encontró el usuario: " + nombreUsuario);
            return;
        }

        Usuario usuarioLogueado = usuarioOpt.get();
        List<EventoSismico> eventos = contexto.getEventosSismicos();

        // Crear el gestor
        GestorRevisionManual gestor = new GestorRevisionManual(eventos, usuarioLogueado);

        // Si tu gestor necesita también los sismógrafos, podés agregar esto:
        // gestor.setSismografos(contexto.getSismografos());

        // Por ahora solo devolvemos una respuesta simple
        ctx.json(java.util.Map.of(
                "mensaje", "Gestor de revisión manual creado correctamente",
                "usuario", usuarioLogueado.getNombreUsuario(),
                "totalEventos", eventos.size()
        ));
    }
}
