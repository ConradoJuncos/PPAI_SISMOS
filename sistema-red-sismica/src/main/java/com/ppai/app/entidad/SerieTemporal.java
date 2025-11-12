package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ppai.app.entidad.MuestraSismica;

public class SerieTemporal {
    
    // Atributos
    private long idSerieTemporal;
    private String condicionAlarma;
    private LocalDateTime fechaHoraRegistro;
    private String frecuenciaMuestreo;
    private Estado estado; 
    private List<MuestraSismica> muestrasSismicas = new ArrayList<>();

    // Constructor sin parámetros
    public SerieTemporal(){}

    /**
     * Obtiene los datos de la serie temporal incluyendo información de muestras sísmicas.
     * Estructura del ArrayList<String>:
     * - Posición 0: ID de serie temporal
     * - Posición 1: Fecha/Hora de registro
     * - Posición 2: Frecuencia de muestreo
     * - Posición 3+: Datos de cada muestra (fechaHora|velocidad|frecuencia|longitud)
     */
    public ArrayList<String> getDatos() {
        return recolectarInformacionSerieTemporal();
    }

    /**
     * Recolecta información de la serie temporal y sus muestras sísmicas asociadas.
     * Retorna un ArrayList<String> con todos los datos organizados de forma jerárquica.
     */
    private ArrayList<String> recolectarInformacionSerieTemporal() {
        ArrayList<String> informacionSerieTemporal = new ArrayList<>();

        // Agregar datos de la serie temporal
        informacionSerieTemporal.add(String.valueOf(this.idSerieTemporal));
        informacionSerieTemporal.add(this.fechaHoraRegistro != null ? this.fechaHoraRegistro.toString() : "");
        informacionSerieTemporal.add(this.frecuenciaMuestreo != null ? this.frecuenciaMuestreo : "");

        // Agregar datos de cada muestra sísmica asociada
        if (this.muestrasSismicas != null) {
            for (MuestraSismica muestra : this.muestrasSismicas) {
                ArrayList<String> datosMuestra = muestra.getDatos();
                // Agregar los datos de la muestra como un string concatenado
                informacionSerieTemporal.add(String.join("|", datosMuestra));
            }
        }

        return informacionSerieTemporal;
    }


    // Métodos Getter y Setter
    public long getIdSerieTemporal(){
        return this.idSerieTemporal;
    }
    public String getCondicionAlarma(){
        return this.condicionAlarma;
    }
    public LocalDateTime getFechaHoraRegistro(){
        return this.fechaHoraRegistro;
    }
    public String getFrecuenciaMuestreo(){
        return this.frecuenciaMuestreo;
    }
    public Estado getEstado(){
        return this.estado;
    }
    public List<MuestraSismica> getMuestrasSismicas(){
        return this.muestrasSismicas;
    }
    public void setIdSerieTemporal(long idSerieTemporal){
        this.idSerieTemporal = idSerieTemporal;
    }
    public void setCondicionAlarma(String condicionAlarma) {
        this.condicionAlarma = condicionAlarma;
    }
    public void setFechaHoraRegistro(LocalDateTime fechaHoraRegistro){
        this.fechaHoraRegistro = fechaHoraRegistro;
    }
    public void setFrecuenciaMuestreo(String frecuenciaMuestreo){
        this.frecuenciaMuestreo = frecuenciaMuestreo;
    }
    public void setEstado(Estado estado){
        this.estado = estado;
    }
    public void setMuestrasSismicas(List<MuestraSismica> muestrasSismicas) {
        this.muestrasSismicas = muestrasSismicas;
    }
}
