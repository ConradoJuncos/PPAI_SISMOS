package com.ppai.app.contexto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ppai.app.entidad.CambioEstado;
import com.ppai.app.entidad.DetalleMuestraSismica;
import com.ppai.app.entidad.Empleado;
import com.ppai.app.entidad.EstacionSismologica;
import com.ppai.app.entidad.Estado;
import com.ppai.app.entidad.EventoSismico;
import com.ppai.app.entidad.MuestraSismica;
import com.ppai.app.entidad.SerieTemporal;
import com.ppai.app.entidad.Sismografo;
import com.ppai.app.entidad.TipoDeDato;
import com.ppai.app.entidad.Usuario;

/**
 * Contexto general del sistema.
 * Carga y mantiene en memoria las entidades principales desde la base de datos.
 */
public class Contexto {

    // DAOs principales
    private final EventoSismicoDAO eventoSismicoDAO = new EventoSismicoDAO();
    private final SerieTemporalDAO serieTemporalDAO = new SerieTemporalDAO();
    private final MuestraSismicaDAO muestraSismicaDAO = new MuestraSismicaDAO();
    private final DetalleMuestraSismicaDAO detalleMuestraSismicaDAO = new DetalleMuestraSismicaDAO();
    private final TipoDeDatoDAO tipoDeDatoDAO = new TipoDeDatoDAO();
    private final SismografoDAO sismografoDAO = new SismografoDAO();
    private final EstacionSismologicaDAO estacionSismologicaDAO = new EstacionSismologicaDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EstadoDAO estadoDAO = new EstadoDAO();

    // Listas en memoria
    private List<EventoSismico> eventosSismicos = new ArrayList<>();
    private List<SerieTemporal> seriesTemporales = new ArrayList<>();
    private List<MuestraSismica> muestrasSismicas = new ArrayList<>();
    private List<DetalleMuestraSismica> detallesMuestra = new ArrayList<>();
    private List<TipoDeDato> tiposDeDato = new ArrayList<>();
    private List<Sismografo> sismografos = new ArrayList<>();
    private List<EstacionSismologica> estaciones = new ArrayList<>();
    private List<Empleado> empleados = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Estado> estados = new ArrayList<>();
    private List<CambioEstado> cambiosEstados = new ArrayList<>();

    public Contexto() {
        try {
            inicializar();
        } catch (SQLException e) {
            System.err.println("Error al inicializar el contexto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void inicializar() throws SQLException {
        System.out.println("Inicializando contexto...");

        estados = estadoDAO.findAll();
        empleados = empleadoDAO.findAll();
        usuarios = usuarioDAO.findAll();
        estaciones = estacionSismologicaDAO.findAll();
        sismografos = sismografoDAO.findAll();
        tiposDeDato = tipoDeDatoDAO.findAll();
        seriesTemporales = serieTemporalDAO.findAll();
        muestrasSismicas = muestraSismicaDAO.findAll();
        detallesMuestra = detalleMuestraSismicaDAO.findAll();
        eventosSismicos = eventoSismicoDAO.findAll();

        vincularEstructuras();

        System.out.println("Contexto inicializado correctamente.");
        imprimirEstructura();  // 👈 se imprime el árbol de relaciones
    }

    // === Getters ===
    public List<EventoSismico> getEventosSismicos() { return eventosSismicos; }
    public List<SerieTemporal> getSeriesTemporales() { return seriesTemporales; }
    public List<MuestraSismica> getMuestrasSismicas() { return muestrasSismicas; }
    public List<DetalleMuestraSismica> getDetallesMuestra() { return detallesMuestra; }
    public List<TipoDeDato> getTiposDeDato() { return tiposDeDato; }
    public List<Sismografo> getSismografos() { return sismografos; }
    public List<EstacionSismologica> getEstaciones() { return estaciones; }
    public List<Empleado> getEmpleados() { return empleados; }
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Estado> getEstados() { return estados; }
    public List<CambioEstado> getCambiosEstados() { return cambiosEstados; }
    public Usuario getUsuarioLogueado() { return usuarios.isEmpty() ? null : usuarios.get(0); }

    // === Vinculación de entidades ===
private void vincularEstructuras() throws SQLException {
    System.out.println("Vinculando entidades...");

    // Mapas de relaciones (FK)
    Map<Integer, Integer> eventoPorSerie = serieTemporalDAO.getRelacionesEventoSerie();
    Map<Integer, Integer> seriePorMuestra = muestraSismicaDAO.getRelacionesSerieMuestra();
    Map<Integer, Integer> muestraPorDetalle = detalleMuestraSismicaDAO.getRelacionesMuestraDetalle();

    // 🔍 DEBUG: Mostrar contenido real de los mapas
    System.out.println("Relaciones evento-serie: " + eventoPorSerie);
    System.out.println("Relaciones serie-muestra: " + seriePorMuestra);
    System.out.println("Relaciones muestra-detalle: " + muestraPorDetalle);

    // Agrupar detalles por muestra
    Map<Integer, List<DetalleMuestraSismica>> detallesDeMuestra = new HashMap<>();
    for (DetalleMuestraSismica detalle : detallesMuestra) {
        Integer idMuestra = muestraPorDetalle.get(detalle.getIdDetalleMuestraSismica());
        if (idMuestra != null)
            detallesDeMuestra.computeIfAbsent(idMuestra, k -> new ArrayList<>()).add(detalle);
    }

    // Asignar detalles a muestras
    for (MuestraSismica muestra : muestrasSismicas) {
        List<DetalleMuestraSismica> detalles = detallesDeMuestra.get(muestra.getIdMuestraSismica());
        if (detalles != null)
            muestra.setDetallesMuestraSismica(detalles);
    }

    // Agrupar muestras por serie
    Map<Integer, List<MuestraSismica>> muestrasDeSerie = new HashMap<>();
    for (MuestraSismica muestra : muestrasSismicas) {
        Integer idSerie = seriePorMuestra.get(muestra.getIdMuestraSismica());
        if (idSerie != null)
            muestrasDeSerie.computeIfAbsent(idSerie, k -> new ArrayList<>()).add(muestra);
    }

    // Asignar muestras a series
    for (SerieTemporal serie : seriesTemporales) {
        List<MuestraSismica> muestras = muestrasDeSerie.get((int) serie.getIdSerieTemporal());
        if (muestras != null)
            serie.setMuestrasSismicas(muestras);
    }

    // Agrupar series por evento
    Map<Integer, List<SerieTemporal>> seriesDeEvento = new HashMap<>();
    for (SerieTemporal serie : seriesTemporales) {
        Integer idEvento = eventoPorSerie.get((int) serie.getIdSerieTemporal());
        if (idEvento != null)
            seriesDeEvento.computeIfAbsent(idEvento, k -> new ArrayList<>()).add(serie);
    }

    // Asignar series a eventos
    for (EventoSismico evento : eventosSismicos) {
        List<SerieTemporal> lista = seriesDeEvento.get((int) evento.getIdEventoSismico());
        if (lista != null) {
            ArrayList<SerieTemporal> series = new ArrayList<>(lista);
            evento.setSeriesTemporales(series);
        }
    }

    System.out.println("Relaciones vinculadas correctamente.");

    // 🔍 DEBUG: Mostrar resumen de cada evento
    System.out.println("\n══════════════════════════════════════════════════════════════════════");
    System.out.println("ESTRUCTURA DE OBJETOS MATERIALIZADOS EN MEMORIA");
    System.out.println("══════════════════════════════════════════════════════════════════════");
    for (EventoSismico e : eventosSismicos) {
        System.out.println("EventoSismico ID=" + e.getIdEventoSismico()
            + " | Fecha=" + e.getFechaHoraOcurrencia()
            + " | Series=" + e.getSeriesTemporales().size());
    }
    System.out.println("══════════════════════════════════════════════════════════════════════");
}


    // === Impresión de estructura para depuración ===
    private void imprimirEstructura() {
        System.out.println("\n══════════════════════════════════════════════════════════════════════");
        System.out.println("ESTRUCTURA DE OBJETOS MATERIALIZADOS EN MEMORIA");
        System.out.println("══════════════════════════════════════════════════════════════════════");

        for (EventoSismico evento : eventosSismicos) {
            System.out.println("EventoSismico ID=" + evento.getIdEventoSismico() +
                               " | Fecha=" + evento.getFechaHoraOcurrencia() +
                               " | Series=" + (evento.getSeriesTemporales() != null ? evento.getSeriesTemporales().size() : 0));
            if (evento.getSeriesTemporales() != null) {
                for (SerieTemporal serie : evento.getSeriesTemporales()) {
                    System.out.println("  └── SerieTemporal ID=" + serie.getIdSerieTemporal() +
                                       " | Frecuencia=" + serie.getFrecuenciaMuestreo() +
                                       " | Muestras=" + (serie.getMuestrasSismicas() != null ? serie.getMuestrasSismicas().size() : 0));
                    if (serie.getMuestrasSismicas() != null) {
                        for (MuestraSismica muestra : serie.getMuestrasSismicas()) {
                            System.out.println("      └── MuestraSismica ID=" + muestra.getIdMuestraSismica() +
                                               " | Detalles=" + (muestra.getDetalleMuestrasSismicas() != null ? muestra.getDetalleMuestrasSismicas().size() : 0));
                            if (muestra.getDetalleMuestrasSismicas() != null) {
                                for (DetalleMuestraSismica det : muestra.getDetalleMuestrasSismicas()) {
                                    TipoDeDato tipo = det.getTipoDeDato();
                                    System.out.println("          └── DetalleMuestra ID=" + det.getIdDetalleMuestraSismica() +
                                                       " | Valor=" + det.getValor() +
                                                       (tipo != null ? " | TipoDato=" + tipo.getDenominacion() : ""));
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("══════════════════════════════════════════════════════════════════════\n");
    }
}
