package com.ppai.app.contexto;

import com.ppai.app.dao.*;
import com.ppai.app.entidad.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public Contexto() {
        try {
            inicializar();
        } catch (SQLException e) {
            System.err.println("Error al inicializar el contexto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void inicializar() throws SQLException {
        // Cargar datos desde la base
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

        System.out.println("Contexto inicializado correctamente.");
    }

    // Getters para acceso a las colecciones en memoria
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

    // Metodo especial provisorio (usuario logueado del sistema)
    public Usuario getUsuarioLogueado(){
        return usuarios.get(0); // Obtener el primero usuario de la lista
    }
}
