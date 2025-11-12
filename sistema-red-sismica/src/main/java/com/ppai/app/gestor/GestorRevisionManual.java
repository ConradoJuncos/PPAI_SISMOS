package com.ppai.app.gestor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ppai.app.entidad.EventoSismico;
import com.ppai.app.entidad.Usuario;
import com.ppai.app.frontend.PantallaRevisionManual;

public class GestorRevisionManual {

    // Atributos del gestor
    private LocalDateTime fechaHoraOcurrenciaEventoSismico;
    private double latitudEpicentroEventoSismico;
    private double longitudEpicentroEventoSismico;
    private double longitudHipocentroEventoSismico;
    private EventoSismico seleccionEventoSismico;
    private List<EventoSismico> eventosSismicos = new ArrayList<EventoSismico>();
    private List<String> datosPrincipalesEventosSismicosNoRevisados = new ArrayList<String>();
    private List<Object> metadatosEventoSismicoSeleccionado = new ArrayList<Object>();
    private List<Object> informacionSismicaEventoSeleccionado = new ArrayList<Object>();
    private String opVisualizacion;
    private String opRechazoModificacion;
    private LocalDateTime fechaHoraActual;
    private Usuario usuarioLogueado;

    // Referencia a la pantalla (interfaz)
    private PantallaRevisionManual pantalla;

    // Constructor
    public GestorRevisionManual(PantallaRevisionManual pantalla, List<EventoSismico> eventosSismicos, Usuario usuarioLogueado) {
        this.pantalla = pantalla;
        this.eventosSismicos = eventosSismicos;
        this.usuarioLogueado = usuarioLogueado;

        // Para debug
        System.out.println("Gestor Revision Manual creado, con estos eventos sismicos: ");
        System.out.println(this.eventosSismicos);

        // EJECUTANDO EL METODO PRIMERO
        buscarEventosSismicosAutoDetectadosNoRevisados();
    }

    private void buscarEventosSismicosAutoDetectadosNoRevisados() {

        // mensaje para debugeo
        System.out.println("Buscando eventos sismicos auto detectados no revisados....");

        // Recorriendo todos los eventos sismicos y agregadolos al listado de no
        // revisados
        for (EventoSismico eventoSismico : eventosSismicos) {

            // Para debug
            System.out.println("estado actual del evento sismico recorrido: ");
            System.out.println(eventoSismico.getEstadoActual().getNombreEstado());

            // Verificar si el evento es auto detecado, y obtenerlo
            if (eventoSismico.esAutoDetectado() && eventoSismico.sosNoRevisado()) {

                // Se agregan sus datos principales a la respuesta
                datosPrincipalesEventosSismicosNoRevisados.add(eventoSismico.obtenerDatosPrincipales());
            }

        }

        // Ordenando los eventos sismicos auto detectados no revisados por fecha de
        // ocurrencia
        ordenarPorFechaHoraOcurrencia();

        // Mostrar los datos principales en la interfaz (en lugar de consola)
        pantalla.mostrarEventosSismicosYSolicitarSeleccion(this.datosPrincipalesEventosSismicosNoRevisados);
        
        // Solo si no hay pantalla (modo debug)
        System.out.println(datosPrincipalesEventosSismicosNoRevisados);

        // Obtener metadatos
        // obtenerYMostrarDatosEventoSelecciando();

    }

    // Ordenar los eventos sismicos autodetectados no revisados por fecha y hora de
    // ocurrencia
    private void ordenarPorFechaHoraOcurrencia() {
        System.out.println("Ordenando los eventos sísmicos obtenidos por fecha de ocurrencia...");

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm")
                .optionalStart().appendPattern(":ss").optionalEnd()
                .toFormatter();

        datosPrincipalesEventosSismicosNoRevisados.sort((s1, s2) -> {
            String fechaHoraStr1 = s1.split(",")[0].trim();
            String fechaHoraStr2 = s2.split(",")[0].trim();

            LocalDateTime fechaHora1 = LocalDateTime.parse(fechaHoraStr1, formatter);
            LocalDateTime fechaHora2 = LocalDateTime.parse(fechaHoraStr2, formatter);

            return fechaHora1.compareTo(fechaHora2);
        });
    }

    // Tomar los datos del evento sismico seleccionado por el analista de sismos
    public void tomarSeleccionEventoSismico(String datosPrincipales) {
        this.seleccionEventoSismico = obtenerEventoSismicoSeleccionado(datosPrincipales);
        bloquearEventoSismicoSeleccionado();
    }

    // Obtener el evento sismico seleccionado por el analista de sismos
    private EventoSismico obtenerEventoSismicoSeleccionado(String datosPrincipales) {
        // Buscar el evento sismico a partir de los datos principales
        for (EventoSismico eventoSismico : eventosSismicos) {

            // Comprobando si los datos principales son del evento sismico iterado
            if (eventoSismico.sonMisDatosPrincipales(datosPrincipales) == true) {
                return eventoSismico;
            }
        }
        return null;

    }

    private void bloquearEventoSismicoSeleccionado() {
        this.fechaHoraActual = getFechaHoraActual();
        this.seleccionEventoSismico.bloquearPorRevision(this.seleccionEventoSismico, this.fechaHoraActual, this.usuarioLogueado);

        // Después de bloquear, obtener y mostrar datos
        obtenerYMostrarDatosEventoSeleccionado();
    }

    // Preparar los datos a mostrar del evento sismico seleccionado
    private void obtenerYMostrarDatosEventoSeleccionado() {
        System.out.println("Obteniendo y mostrando datos del evento seleccionado...");

        // 1. Obtener metadatos del evento seleccionado
        obtenerMetadatosEventoSeleccionado();

        // 2. Extraer información sísmica del evento seleccionado
        // ESTO ES EL TRIPLE FOR
        extraerInformacionSismicaEventoSeleccionado();

        // 3. Clasificar información por estación sismológica
        List<Object> informacionClasificada = clasificarPorEstacionSismologica();

        // 4. Llamar al caso de uso 18 abstracto - Generar Sismograma
        generarSismogramaPorEstacionSismologica(informacionClasificada);

        // 5. Mostrar los datos por pantalla
        mostrarDatosSismicosRegistrados();

        // 6. Habilitar la Opción de Visualizar Mapa de Eventos
        pantalla.habilitarVisualizacionMapa();
    }

    private void obtenerMetadatosEventoSeleccionado() {
        this.metadatosEventoSismicoSeleccionado = seleccionEventoSismico.obtenerMetadatosEventoSeleccionado();
        System.out.println("Metadatos obtenidos: " + metadatosEventoSismicoSeleccionado);
    }

    // Extraer información sísmica del evento seleccionado
    private void extraerInformacionSismicaEventoSeleccionado() {
        System.out.println("Extrayendo información sísmica del evento seleccionado...");
        this.informacionSismicaEventoSeleccionado = seleccionEventoSismico.extraerInformacionSismica();
    }

    // Clasificar información por estación sismológica
    private List<Object> clasificarPorEstacionSismologica() {
        System.out.println("Clasificando información por estación sismológica...");
        // Aquí se organizaría la información por estación
        // Por ahora retornamos la información tal cual
        return this.informacionSismicaEventoSeleccionado;
    }

    // Generar sismograma por estación sismológica (CU18 - abstracto)
    private void generarSismogramaPorEstacionSismologica(List<Object> informacionClasificada) {
        System.out.println("Generando sismogramas por estación sismológica...");
        // Este es un caso de uso abstracto que se ejecutaría aquí
        // Por ahora solo lo simulamos con un mensaje
    }

    // Mostrar los datos sismicos registrados, pasando los parametros correspondientes
    private void mostrarDatosSismicosRegistrados() {
        System.out.println("Mostrando datos sísmicos registrados en pantalla...");
        pantalla.mostrarDatosSismicosRegistrados(
            metadatosEventoSismicoSeleccionado,
            informacionSismicaEventoSeleccionado
        );
    }

    // Tomar opcion no visualizacion de sismograma por estacion sismlogica
    public void tomarNoVisualizacion() {

        // ejectuar el metodo de soliciatar modificacion datos simsicos de la pantalla

    }

    public void tomarRechazoModificacion() {

        // LLamar al metodo solicitarOpcAccionEvento() de la pantalla
    }

    // Rechazar el evento sismico anteriormente seleccioando por el usuario
    public void rechazarEventoSismicoSeleccionado() {

        // 1. Validar los datos sismicos si hubieran sido modificados

        // 2. rechazar el evento sismico llamando al metodo
        // recharEventoSismicoSeleccioando(): void

        // 3. llamar a fin caso de uso finCU()
    }

    // Obtener fecha y hora actual del sistema
    private LocalDateTime getFechaHoraActual() {

        // Se retorna la fecha y hora actualizada del sistema
        return actualizarFechaHoraActual();
    }

    // Obtener la fecha y hora actualizada
    private LocalDateTime actualizarFechaHoraActual() {
        return LocalDateTime.now();
    }
}
