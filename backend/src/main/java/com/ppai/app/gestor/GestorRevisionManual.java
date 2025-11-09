package com.ppai.app.gestor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ppai.app.entidad.EventoSismico;
import com.ppai.app.entidad.Usuario;

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

    public GestorRevisionManual(List<EventoSismico> eventosSismicos, Usuario usuarioLogueado) {
        this.eventosSismicos = eventosSismicos;
        this.usuarioLogueado = usuarioLogueado;
    }

    // Buscar todos los eventos sismicos con estado auto detectado y que no tengan analista asignado (no revisado)
    private void buscarEventosSismicosAutoDetectadosNoRevisados() {

        // Recorriendo todos los eventos sismicos y agregadolos al listado de no revisados
        for (EventoSismico eventoSismico : eventosSismicos) {

            // Verificar si el evento es auto detecado, y obtenerlo
            if (eventoSismico.esAutoDetectado() && eventoSismico.sosNoRevisado()) {

                // Se agregan sus datos principales a la respuesta
                datosPrincipalesEventosSismicosNoRevisados.add(eventoSismico.obtenerDatosPrincipales());
            }

        }

        // Ordenando los eventos sismicos auto detectados no revisados por fecha de ocurrencia
        ordenarPorFechaHoraOcurrencia();

        // Mostrando los datos principales de los eventos sismicos auto detectados y no revisados ordenados por fecha de ocurrencia
        // pantalla.mostrarEventosSismicosYSolicitarSeleccion(this.datosPrincipalesEventosSismicosNoRevisados); 
    }


    // Ordenar los eventos sismicos autodetectados no revisados por fecha y hora de ocurrencia
    private void ordenarPorFechaHoraOcurrencia() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

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

        // Identificar y obtener al evento sismico seleccionado por el analista a partir de sus datos principales
        // Y comenzar el proceso de bloqueo del evento más la obtención y muestreo de sus datos.
        obtenerEventoSismicoSeleccionado(datosPrincipales);
        
    }

    // Obtener el evento sismico seleccionado por el analista de sismos
    private void obtenerEventoSismicoSeleccionado(String datosPrincipales) {

        // Buscar el evento sismico a partir de los datos principales
        for (EventoSismico eventoSismico : eventosSismicos){
            
            // Comprobando si los datos principales son del evento sismico iterado
            if (eventoSismico.sonMisDatosPrincipales(datosPrincipales) != null){

                // Se asigna el evento sismico seleccioando por el analista
                this.seleccionEventoSismico = eventoSismico.sonMisDatosPrincipales(datosPrincipales);
            }
        }
        
        // Cambiar el estado del evento sismico seleccionado a bloqueado en revision (Bloquear el evento sismico)


        // 3. Llamar al metodo obtenerYMostrarDatosEventoSeleccionado():void
        // Por ahora mostrando en terminal
        System.out.println(datosPrincipales);

    }

    // Bloquear el eveto sismico seleccioando por el analista
    private void bloquearEventoSismicoSeleccionado() {

        // Obtener la fecha y hora actual del sistema
        this.fechaHoraActual = getFechaHoraActual();

        // Bloquear el evento sismico seleccionado por en analista con motivo de revision
        this.seleccionEventoSismico.bloquearPorRevision(this.seleccionEventoSismico, this.fechaHoraActual, this.usuarioLogueado);

    }

    // Preparar los datos a mostar del vento sismico seleccionado
    private void obtenerYMostrarDatosEventoSelecciando() {

        // 1. Llamar al metodo obtenerMetadadosEventoSeleccionado(): void

        // 2. Llamar al metodo extraerInformacionSismicaEventoSelecciondo(): Object[]

        // 3. Clasificar inforamcion por estacion sismologica
        // llamando al método clasificarPorEstacionSismologica (private)

        // 4. Llamar al caso de uso 18 abstracto,
        // mediante el metodo generarSismogramaPorEstaacionSismologica()

        // 5. Mostrar los datos por pantalla llamando al metodo de la pantalla
        // mostrarDatosSismicosRegistrados(), con los parámetros correspondientes

        // 6. Habilitar la Opcion de Visualizar Mapa de Eventos
    }

    // Mostrar los datos sismicos registrados, pasando los parametros
    // correspondientes
    private void mostrarDatosSismicosRegistrados() {

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
    private LocalDateTime getFechaHoraActual(){

        // Se retorna la fecha y hora actualizada del sistema
        return actualizarFechaHoraActual();
    }

    // Obtener la fecha y hora actualizada
    private LocalDateTime actualizarFechaHoraActual(){
        return LocalDateTime.now();
    }
}
