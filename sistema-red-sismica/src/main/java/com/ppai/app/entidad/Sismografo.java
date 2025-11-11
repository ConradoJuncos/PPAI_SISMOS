package com.ppai.app.entidad;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Sismografo {

    // Atributos
    private long identificadorSismografo;
    private LocalDateTime fechaAdquicision;
    private long nroSerie;
    private List<SerieTemporal> serieTemporal = new ArrayList<SerieTemporal>();
    private Estado estadoActual;
    private List<CambioEstado> cambioEstado = new ArrayList<CambioEstado>();
    private List<Reparacion> reparacion = new ArrayList<Reparacion>();
    private EstacionSismologica estacionSismologica;
    private ModeloSismografo modelo;

    // Método constructor sin parámetros
    public Sismografo(){}

    // Comportamiento 

    /* Este método permite obtener el código y nombre de la estacion sismologica del sismógrafo
     * en caso de que el id de serie temporal pasado por parámetro coincida con el id de alguna de 
     * las series temporales asociadas al sismógrafo. */
    public List<Object> esTuSerieTemporal(long idSerieTemporal){
        
        // Recorriendo todas las series temporales
        for (SerieTemporal serie: serieTemporal){

            // Verificando si el id de serie temporal coincide con alguna de las series temporales propias
            if (serie.getIdSerieTemporal() == idSerieTemporal){
                
                // Creando un array de object para los datos de la estacion simsologica del sismografo
                List<Object> datosEstacionSismologica = new ArrayList<Object>();

                // Colocando el código de la estacion sismologica en la primera posicion
                datosEstacionSismologica.add(this.estacionSismologica.getCodigoEstacion());

                // Colocando el nombre de la estacion sismologica en la segunda posicion
                datosEstacionSismologica.add(this.estacionSismologica.getNombre());

                return datosEstacionSismologica;
            }
        }

        // En caso de que el id de serie temporal no sea de este sismografo
        return null;
        
    }

    // Métodos Getter y Setter
    public long getIdentificadorSismografo(){
        return this.identificadorSismografo;
    }
    public LocalDateTime getFechaAdquisicion(){
        return this.fechaAdquicision;
    }
    public long getNroSerie(){
        return this.nroSerie;
    } 
    public List<SerieTemporal> getSerieTemporal(){
        return this.serieTemporal;
    }
    public Estado getEstadoActual(){
        return this.estadoActual;
    }
    public List<CambioEstado> getCambioEstado(){
        return this.cambioEstado;
    }
    public List<Reparacion> getReparacion(){
        return this.reparacion;
    }
    public EstacionSismologica getEstacionSismologica(){
        return this.estacionSismologica;
    }
    public ModeloSismografo getModelo(){
        return this.modelo;
    }
    public void setIdentificadorSismografo(long identificadorSismografo){
        this.identificadorSismografo = identificadorSismografo;
    }
    public void setFechaAdquisicion(LocalDateTime fechaAdquisicion){
        this.fechaAdquicision = fechaAdquisicion;
    }
    public void setNroSerie(long nroSerie){
        this.nroSerie = nroSerie;
    }
    public void setSerieTemporal(List<SerieTemporal> serieTemporal){
        this.serieTemporal = serieTemporal;
    }
    public void setEstadoActual(Estado estadoActual){
        this.estadoActual = estadoActual;
    }
    public void setCambioEstado(List<CambioEstado> cambioEstado){
        this.cambioEstado = cambioEstado;
    }
    public void setReparacion(List<Reparacion> reparacion){
        this.reparacion = reparacion;
    }
    public void setEstacionSismologica(EstacionSismologica estacionSismologica){
        this.estacionSismologica = estacionSismologica;
    }
    public void setModelo(ModeloSismografo modelo){
        this.modelo = modelo;
    }
}
