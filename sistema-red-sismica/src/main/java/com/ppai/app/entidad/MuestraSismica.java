package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ppai.app.dto.MuestraSismicaDTO;

public class MuestraSismica {
    
    // Atributos
    private long idMuestraSismica;
    private LocalDateTime fechaHoraMuestraSismica;
    private List<DetalleMuestraSismica> detalleMuestrasSismicas = new ArrayList<>();

    // Constructor sin parámetros
    public MuestraSismica(){}

    // Comportamiento
    public MuestraSismicaDTO getDatos(){

        // Se recolecta y entrega la informacion empaquetada de la muestra sismica
        return recolectarInformacionMuestraSismica();
    }

    public MuestraSismicaDTO recolectarInformacionMuestraSismica(){

        // Se obtiene la fecha y hora de la muestra sismica
        LocalDateTime fechaHora = getFechaHoraMuestraSismica();

        // Se definen los valores de los detalles de la muestra sismica
        Double velocidadOnda = null;
        Double frecuenciaOnda = null;
        Double longitudOnda = null;

        // Se recorren los detalles asociados a la muestra sismica
        for (DetalleMuestraSismica detalle : detalleMuestrasSismicas) {

            // Se validan y obtiene los valores de los detalles con tipos de dato con denominacion de interes
            if (detalle.sosDenominacionTipoDeDatoVelocidadOnda()) { velocidadOnda = detalle.getValor(); }
            if (detalle.sosDenominacionTipoDeDatoFrecuenciaOnda()) { frecuenciaOnda = detalle.getValor(); }
            if (detalle.sosDenominacionTipoDeDatoLongitudOnda()) { longitudOnda = detalle.getValor(); }
        }

        return prepararInformacionMuestraSismicaDTO(fechaHora, velocidadOnda, frecuenciaOnda, longitudOnda);

    }

    // Empaquetar y preparar la informacion de la muestra sismica
    public MuestraSismicaDTO prepararInformacionMuestraSismicaDTO(LocalDateTime fechaHora, double velocidadOnda, double frecuenciaOnda, double longitudOnda) {

        return new MuestraSismicaDTO(fechaHora, velocidadOnda, frecuenciaOnda, longitudOnda);
    }

    public void crearDetalleMuestra(){
        // Implementar la lógica de este método
    }

    // Métodos Getter y Setter
    public long getIdMuestraSismica(){
        return this.idMuestraSismica;
    }
    public LocalDateTime getFechaHoraMuestraSismica(){
        return this.fechaHoraMuestraSismica;
    }
    public List<DetalleMuestraSismica> getDetalleMuestrasSismicas(){
        return this.detalleMuestrasSismicas;
    }
    public void setIdMuestraSismica(long idMuestraSismica) {
        this.idMuestraSismica = idMuestraSismica;
    }
    public void setFechaHoraMuestraSismica(LocalDateTime fechaHoraMuestraSismica){
        this.fechaHoraMuestraSismica = fechaHoraMuestraSismica;
    }
    public void setDetalleMuestrasSismicas(List<DetalleMuestraSismica> detalleMuestrasSismicas){
        this.detalleMuestrasSismicas = detalleMuestrasSismicas;
    }
}
