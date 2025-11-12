package com.ppai.app.contexto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ppai.app.dao.DetalleMuestraSismicaDAO;
import com.ppai.app.dao.EmpleadoDAO;
import com.ppai.app.dao.EstacionSismologicaDAO;
import com.ppai.app.dao.EstadoDAO;
import com.ppai.app.dao.EventoSismicoDAO;
import com.ppai.app.dao.MuestraSismicaDAO;
import com.ppai.app.dao.SerieTemporalDAO;
import com.ppai.app.dao.SismografoDAO;
import com.ppai.app.dao.TipoDeDatoDAO;
import com.ppai.app.dao.UsuarioDAO;
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

    // Metodo especial provisorio (usuario logueado del sistema)
    public Usuario getUsuarioLogueado(){
        return usuarios.get(0); // Obtener el primero usuario de la lista
    }
}
