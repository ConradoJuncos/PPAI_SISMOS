package com.ppai.app.gestor;

import java.time.LocalDateTime;
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
    private List<Object> metadatosEventoSismicoSeleccionado = new ArrayList<Object>();
    private List<Object> informacionSismicaEventoSeleccionado = new ArrayList<Object>();
    private String opVisualizacion;
    private String opRechazoModificacion;
    private LocalDateTime fechaHoraActual;
    private Usuario usuarioLogueado;
    
    /**
     * Constructor público - NO es Singleton.
     * Puede recibir dependencias por parámetro es necesario.
     */
    public GestorRevisionManual() {
        // TODO: Inicializar tus dependencias
    }
    

    // Buscar todos los eventos sismicos con estado auto detectado y que no tengan analista asignado (no revisados)
    private void buscarEventosSismicosAutoDetectadosNoRevisados(){

    }

    // Ordenar los eventos sismicos autodetectados no revisados por fecha y hora de ocurrencia
    private void ordenarPorFechaHoraOcurrencia(){

    } 


    // Tomar los datos del evento sismico seleccionado por el analista de sismos
    public void tomarSeleccionEventoSismico(String datosPrincipales){
        
        // Llamar al metodo obtenerEventosSismicoSeleccioando
    }

    private void obtenerEventoSismicoSeleccionado(String datosPrincipales){

        // 1. Llamar reiteradamente al método sonMisDatosPrincipales(String datosPrincipales): EventoSismico

        // 2. llamar al método bloquearEventoSismicoSeleccionado():void (private)
    
    }

    // Bloquear el eveto sismico seleccioando por el analista
    private void bloquearEventoSismicoSeleccionado(){

        // 1. llamar al metodo getFechaHoraActual(): LocalDateTime (resolver la obtencion de la hora real) (private)

        // 2. llamar al metodo bloquearPorRevision(this.seleccioandoEventoSismico, this.fechaHoraActual, this.usuarioLogueado)
        // del evento sismico seleccionado
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
    }

    // Rechazar el evento sismico anteriormente seleccioando por el usuario
    public void rechazarEventoSismicoSeleccionado(){

        // 1. Validar los datos sismicos si hubieran sido modificados

        // 2. rechazar el evento sismico llamando al metodo recharEventoSismicoSeleccioando(): void

        // 3. llamar a fin caso de uso finCU()
    }

}

