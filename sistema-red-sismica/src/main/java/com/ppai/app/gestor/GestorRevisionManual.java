package com.ppai.app.gestor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ppai.app.entidad.Empleado;
import com.ppai.app.entidad.EventoSismico;
import com.ppai.app.entidad.Usuario;
import com.ppai.app.entidad.Sismografo;
import com.ppai.app.entidad.CambioEstado;
import com.ppai.app.dao.CambioEstadoDAO;
import com.ppai.app.dao.EventoSismicoDAO;
import com.ppai.app.frontend.PantallaRevisionManual;

public class GestorRevisionManual {

    // Atributos del gestor
    private LocalDateTime fechaHoraOcurrenciaEventoSismico;
    private double latitudEpicentroEventoSismico;
    private double longitudEpicentroEventoSismico;
    private double longitudHipocentroEventoSismico;
    private EventoSismico seleccionEventoSismico;
    private List<EventoSismico> eventosSismicos = new ArrayList<>();
    private List<Sismografo> sismografos = new ArrayList<>();
    private List<String> datosPrincipalesEventosSismicosNoRevisados = new ArrayList<>();
    private List<String> metadatosEventoSismicoSeleccionado = new ArrayList<>();
    private List<ArrayList<String>> informacionSismicaEventoSeleccionado = new ArrayList<>();
    private String opVisualizacion;
    private String opRechazoModificacion;
    private LocalDateTime fechaHoraActual;
    private Usuario usuarioLogueado;

    // Referencia a la pantalla (interfaz)
    private PantallaRevisionManual pantalla;

    // DAOs para persistencia
    private CambioEstadoDAO cambioEstadoDAO;
    private EventoSismicoDAO eventoSismicoDAO;

    // Constructor
    public GestorRevisionManual(PantallaRevisionManual pantalla, List<EventoSismico> eventosSismicos,
                                  List<Sismografo> sismografos, Usuario usuarioLogueado) {
        this.pantalla = pantalla;
        this.eventosSismicos = eventosSismicos;
        this.sismografos = sismografos;
        this.usuarioLogueado = usuarioLogueado;

        // Inicializar DAOs
        this.cambioEstadoDAO = new CambioEstadoDAO();
        this.eventoSismicoDAO = new EventoSismicoDAO();

        // Comenzando el proceso de buscar los eventos sismicos auto detectados no reviados
        buscarEventosSismicosAutoDetectadosNoRevisados();
    }

    /**
     * Busca todos los eventos sísmicos auto detectados que aún no han sido revisados.
     * Ordena por fecha/hora de ocurrencia y muestra en pantalla.
     */
    private void buscarEventosSismicosAutoDetectadosNoRevisados() {
        System.out.println("Buscando eventos sismicos auto detectados no revisados....");

        // Recorrer todos los eventos sísmicos
        for (EventoSismico eventoSismico : eventosSismicos) {

            // Verificar si es auto detectado y no revisado
            if (eventoSismico.esAutoDetectado() && eventoSismico.sosNoRevisado()) {
                datosPrincipalesEventosSismicosNoRevisados.add(eventoSismico.obtenerDatosPrincipales());
            }
        }

        // Ordenar por fecha/hora de ocurrencia
        ordenarPorFechaHoraOcurrencia();

        // Mostrar en pantalla
        pantalla.mostrarEventosSismicosYSolicitarSeleccion(this.datosPrincipalesEventosSismicosNoRevisados);
    }

    /**
     * Ordena los eventos sísmicos por fecha y hora de ocurrencia (ascendente).
     */
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

    /**
     * Toma la selección del evento sísmico realizada por el usuario.
     */
    public void tomarSeleccionEventoSismico(String datosPrincipales) {
        this.seleccionEventoSismico = obtenerEventoSismicoSeleccionado(datosPrincipales);
        bloquearEventoSismicoSeleccionado();
    }

    /**
     * Obtiene el evento sísmico seleccionado a partir de sus datos principales.
     */
    private EventoSismico obtenerEventoSismicoSeleccionado(String datosPrincipales) {
        for (EventoSismico eventoSismico : eventosSismicos) {
            if (eventoSismico.sonMisDatosPrincipales(datosPrincipales) == true) {
                return eventoSismico;
            }
        }
        return null;
    }

    /**
     * Bloquea el evento sísmico seleccionado para revisión y obtiene sus datos.
     */
    private void bloquearEventoSismicoSeleccionado() {
        this.fechaHoraActual = getFechaHoraActual();
        this.seleccionEventoSismico.bloquearPorRevision(this.seleccionEventoSismico, this.fechaHoraActual, this.usuarioLogueado);
        obtenerYMostrarDatosEventoSeleccionado();
    }

    /**
     * Obtiene y muestra todos los datos del evento seleccionado.
     */
    private void obtenerYMostrarDatosEventoSeleccionado() {
        this.setMetadatosEventoSismicoSeleccionado(obtenerMetadatosEventoSeleccionado());

        // Extraer información sísmica del evento seleccionado
        extraerInformacionSismicaEventoSeleccionado();

        // Clasificar información por estación sismológica
        List<ArrayList<String>> informacionClasificada = clasificarPorEstacionSismologica();

        // Generar sismogramas
        generarSismogramaPorEstacionSismologica(informacionClasificada);

        // Mostrar los datos por pantalla
        mostrarDatosSismicosRegistrados(informacionClasificada);

        // Habilitar la Opción de Visualizar Mapa de Eventos
        pantalla.habilitarVisualizacionMapa();
    }

    /**
     * Obtiene los metadatos del evento sísmico seleccionado.
     */
    private List<String> obtenerMetadatosEventoSeleccionado() {
        return seleccionEventoSismico.obtenerMetadatosEventoSeleccionado();
    }

    /**
     * Extrae información sísmica del evento seleccionado.
     * Obtiene todas las series temporales y sus muestras asociadas.
     */
    private void extraerInformacionSismicaEventoSeleccionado() {
        System.out.println("Extrayendo información sísmica del evento seleccionado...");
        this.informacionSismicaEventoSeleccionado = seleccionEventoSismico.extraerInformacionSismica();
    }

    private List<ArrayList<String>> clasificarPorEstacionSismologica() {
        List<ArrayList<String>> informacionClasificada = new ArrayList<>();

        for (ArrayList<String> datosSerie : informacionSismicaEventoSeleccionado) {
            long idSerieTemporal = Long.parseLong(datosSerie.get(0));

            List<Object> datosEstacion = null;
            for (Sismografo sismografo : sismografos) {
                datosEstacion = sismografo.esTuSerieTemporal(idSerieTemporal);

                if (datosEstacion != null) {
                    break;
                }
            }

            // Si encontramos la estación, agregar información completa con datos de estación
            if (datosEstacion != null) {
                ArrayList<String> datosSerieCompletos = new ArrayList<>();
                datosSerieCompletos.add(datosEstacion.get(0).toString());
                datosSerieCompletos.add(datosEstacion.get(1).toString());
                for (String datoSerie : datosSerie) {
                    datosSerieCompletos.add(datoSerie);
                };
                informacionClasificada.add(datosSerieCompletos);
            } else {
                System.out.println("No se encontro el sismografo");
            }
        }

        return informacionClasificada;
    }

    /**
     * Genera sismogramas por estación sismológica.
     * Caso de uso abstracto que se implementaría aquí.
     */
    private void generarSismogramaPorEstacionSismologica(List<ArrayList<String>> informacionClasificada) {
        System.out.println("Generando sismogramas por estación sismológica...");
        // Este es un caso de uso abstracto que se ejecutaría aquí
        // Por ahora solo lo simulamos con un mensaje
    }

    /**
     * Muestra los datos sísmicos registrados en la pantalla.
     * Convierte los datos clasificados a formato string para la pantalla.
     */
    private void mostrarDatosSismicosRegistrados(List<ArrayList<String>> informacionEstaciones) {
        System.out.println("Mostrando datos sísmicos registrados en pantalla...");

        // Convertir la información clasificada a formato string: [[id, nombre, codigo, fecha, frecuencia, muestras...], ...]
        String datosClasificadosString = convertirDatosAString(informacionEstaciones);

        // Mostrar en pantalla con el nuevo método que espera 4 parámetros
        pantalla.mostrarDatosSismicosRegistrados(
            metadatosEventoSismicoSeleccionado.get(0),  // alcance
            metadatosEventoSismicoSeleccionado.get(1),  // clasificación
            metadatosEventoSismicoSeleccionado.get(2),  // origen
            datosClasificadosString                      // datos clasificados en formato string
        );
    }

    /**
     * Convierte la información clasificada a formato string para la pantalla.
     */
    private String convertirDatosAString(List<ArrayList<String>> informacionEstaciones) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < informacionEstaciones.size(); i++) {
            ArrayList<String> estacion = informacionEstaciones.get(i);
            sb.append("[");

            for (int j = 0; j < estacion.size(); j++) {
                sb.append(estacion.get(j));
                if (j < estacion.size() - 1) {
                    sb.append(", ");
                }
            }

            sb.append("]");
            if (i < informacionEstaciones.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Toma la opción de no visualizar sismogramas por estación.
     */
    public void tomarNoVisualizacion() {
        pantalla.solicitarModificaciónDatosSismicos();
    }

    /**
     * Toma el rechazo a la modificación de datos.
     */
    public void tomarRechazoModificacion() {
        pantalla.solicitarOpcAccionEvento();
    }

    // Rechazar el evento sismico anteriormente seleccioando por el usuario
    public void rechazarEventoSismicoSeleccionado() {

        // 1. Validar los datos sismicos si hubieran sido modificados
        this.validarDatosSismicos();

        this.actualizarEventoSismicoARechazado();

        // 3. llamar a fin caso de uso finCU()
        this.finCU();
    }

    // Aceptar el evento sismico anteriormente seleccioando por el usuario
    public void confirmarEventoSismicoSeleccionado() {

        // 1. Validar los datos sismicos si hubieran sido modificados
        this.validarDatosSismicos();

        this.actualizarEventoSismicoAConfirmado();

        // 3. llamar a fin caso de uso finCU()
        this.finCU();
    }

    // Derivar a experto el evento sismico anteriormente seleccioando por el usuario
    public void derivarAExpertoEventoSismicoSeleccionado() {

        // 1. Validar los datos sismicos si hubieran sido modificados
        this.validarDatosSismicos();

        this.actualizarEventoSismicoADerivadoAExperto();

        // 3. llamar a fin caso de uso finCU()
        this.finCU();
    }

    private void finCU() {
        System.out.println("Finalizando caso de uso: persistiendo cambios...");

        try {
            // Obtener los cambios de estado del evento sísmico
            List<CambioEstado> cambiosDelEvento = seleccionEventoSismico.getCambiosEstado();

            if (cambiosDelEvento != null && !cambiosDelEvento.isEmpty()) {
                // Persistir cada cambio de estado nuevo
                for (CambioEstado cambio : cambiosDelEvento) {
                    // Solo persistir si no tiene ID (es nuevo)
                    if (cambio.getIdCambioEstado() == 0) {
                        // Setear el idEventoSismico antes de insertar
                        cambio.setIdEventoSismico(seleccionEventoSismico.getIdEventoSismico());

                        // Insertar el CambioEstado (esto genera el ID)
                        cambioEstadoDAO.insert(cambio);
                        System.out.println("✓ CambioEstado persistido: " + cambio.getEstado().getNombreEstado() + " con ID: " + cambio.getIdCambioEstado());

                        // Persistir la relación en EventoSismico_CambioEstado
                        insertarRelacionEventoCambio(seleccionEventoSismico.getIdEventoSismico(), cambio.getIdCambioEstado());
                    }
                }
                System.out.println("✓ Total de cambios de estado persistidos correctamente.");
            }

            // Persistir el evento sísmico actualizado con su nuevo estado
            eventoSismicoDAO.update(seleccionEventoSismico);
            System.out.println("✓ EventoSismico actualizado correctamente.");

            System.out.println("Caso de uso ejecutado y persistido correctamente.");

        } catch (Exception e) {
            System.err.println("✗ Error al persistir cambios en finCU(): " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al finalizar el caso de uso: " + e.getMessage());
        }
    }

    /**
     * Inserta la relación entre EventoSismico y CambioEstado en la tabla intermedia.
     */
    private void insertarRelacionEventoCambio(long idEventoSismico, long idCambioEstado) throws Exception {
        String sql = "INSERT INTO EventoSismico_CambioEstado (idEventoSismico, idCambioEstado) VALUES (?, ?)";
        try (java.sql.Connection conn = com.ppai.app.datos.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idEventoSismico);
            ps.setLong(2, idCambioEstado);
            ps.executeUpdate();
            System.out.println("✓ Relación EventoSismico_CambioEstado persistida: [" + idEventoSismico + ", " + idCambioEstado + "]");
        }
    }

    private void validarDatosSismicos() {
        try {
            if (seleccionEventoSismico.getMagnitudRichter() == null) {
                throw new Exception("No hay magnitud");
            }
            if (seleccionEventoSismico.getAlcanceSismo() == null) {
                throw new Exception("No hay alcance");
            }
            if (seleccionEventoSismico.getOrigenGeneracion() == null) {
                throw new Exception("No hay origen de generacion");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void actualizarEventoSismicoARechazado() {
        this.seleccionEventoSismico.rechazar(fechaHoraActual, usuarioLogueado);
    }

    private void actualizarEventoSismicoAConfirmado() {
        this.seleccionEventoSismico.confirmar(fechaHoraActual, usuarioLogueado);
    }

    private void actualizarEventoSismicoADerivadoAExperto() {
        this.seleccionEventoSismico.derivarAExperto(fechaHoraActual, usuarioLogueado);
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

    public void setMetadatosEventoSismicoSeleccionado(List<String> metadatos) {
        this.metadatosEventoSismicoSeleccionado = metadatos;
    }
}
