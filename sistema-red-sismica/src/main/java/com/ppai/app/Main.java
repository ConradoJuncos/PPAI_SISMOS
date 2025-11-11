package com.ppai.app;

import com.ppai.app.contexto.Contexto;
import com.ppai.app.datos.DatabaseConnection;
import com.ppai.app.frontend.PantallaRevisionManual;
import com.ppai.app.util.ConsolaSistema;

public class Main {

    public static void main(String[] args) {

        // ===================== CABECERA =====================
        ConsolaSistema.titulo("SISTEMA PPAI RED SISMICA - MODO LOCAL");

        // ===================== 1. BASE DE DATOS =====================
        ConsolaSistema.iniciarBaseDeDatos(() -> {
            DatabaseConnection.initDatabase();
        });

        // ===================== 2. CONTEXTO =====================
        Contexto contexto = new Contexto();
        ConsolaSistema.iniciarContexto(
                () -> {},
                contexto.getEventosSismicos().size(),
                contexto.getUsuarios().size(),
                contexto.getSismografos().size()
        );

        // ===================== 3. INTERFAZ GRAFICA =====================
        ConsolaSistema.info("Abriendo interfaz gráfica...");
        new PantallaRevisionManual(contexto);
        ConsolaSistema.ok("Interfaz gráfica iniciada correctamente.");
    }
}