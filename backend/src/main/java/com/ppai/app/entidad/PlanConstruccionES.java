package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class PlanConstruccionES {
    
    // Atributos
    private long codigoPlanConstruccion;
    private LocalDateTime fechaFinalizacion;
    private LocalDateTime fechaPrevistaInicio;
    private LocalDateTime fechaProbableInicioPruebas;
    private List<TrabajoARealizar> trabajosARealizar = new ArrayList<TrabajoARealizar>();
    private Empleado encargadoInstalacion;
    private Sismografo sismografoAsignado;
    private EstacionSismologica estacionSismologica;

    // Método constructor sin parámetros
    public PlanConstruccionES(){}

    // Comportamiento
    public boolean esVigente(){
        if (this.fechaFinalizacion == null) {
            return true;
        }
        return false;
    }

    // Métodos Getter y Setter
    public long getcodigoPlanConstruccion(){
        return this.codigoPlanConstruccion;
    }
    public LocalDateTime getFechaFinalizacion(){
        return this.fechaFinalizacion;
    }
    public LocalDateTime getFechaPrevistaInicio(){
        return this.fechaPrevistaInicio;
    }
    public LocalDateTime getFechaProbableInicioPruebas(){
        return this.fechaProbableInicioPruebas;
    }
    public List<TrabajoARealizar> getTrabajosARealizar(){
        return this.trabajosARealizar;
    }
    public Empleado getEncargadoInstalacion(){
        return this.encargadoInstalacion;
    }
    public Sismografo getSismografoAsignado(){
        return this.sismografoAsignado;
    }
    public EstacionSismologica getEstacionSismologica(){
        return this.estacionSismologica;
    }
    public void setcodigoPlanConstruccion(long codigoPlanConstruccion){
        this.codigoPlanConstruccion = codigoPlanConstruccion;
    }
    public void setFechaFinalizacion(LocalDateTime fechaFinalizacion){
        this.fechaFinalizacion = fechaFinalizacion;
    }
    public void setFechaPrevistaInicio(LocalDateTime fechaPrevistaInicio){
        this.fechaPrevistaInicio = fechaPrevistaInicio;
    }
    public void setFechaProbableInicioPruebas(LocalDateTime fechaProbableInicioPruebas){
        this.fechaProbableInicioPruebas = fechaProbableInicioPruebas;
    }
    public void setTrabajosARealizar(List<TrabajoARealizar> trabajosARealizar){
        this.trabajosARealizar = trabajosARealizar;
    }
    public void setEncargadoInstalacion(Empleado encargadoInstalacion){
        this.encargadoInstalacion = encargadoInstalacion;
    }
    public void setSismografoAsignado(Sismografo sismografoAsignado){
        this.sismografoAsignado = sismografoAsignado;
    }
    public void setEstacionSismologica(EstacionSismologica estacionSismologica){
        this.estacionSismologica = estacionSismologica;
    }

}
