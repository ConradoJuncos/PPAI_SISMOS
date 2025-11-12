package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MuestraSismica {
    
    // Atributos
    private long idMuestraSismica;
    private LocalDateTime fechaHoraMuestraSismica;
    private List<DetalleMuestraSismica> detalleMuestrasSismicas = new ArrayList<>();

    // Constructor sin parámetros
    public MuestraSismica(){}

    /**
     * Obtiene los datos de la muestra sísmica.
     * Estructura del ArrayList<String>:
     * - Posición 0: Fecha/Hora de la muestra
     * - Posición 1: Velocidad de onda (km/seg)
     * - Posición 2: Frecuencia de onda (Hz)
     * - Posición 3: Longitud de onda (km/ciclo)
     */
    public ArrayList<String> getDatos() {
        return recolectarInformacionMuestraSismica();
    }

    /**
     * Recolecta la información de la muestra sísmica a partir de sus detalles.
     * Retorna un ArrayList<String> con fecha, velocidad, frecuencia y longitud.
     */
    private ArrayList<String> recolectarInformacionMuestraSismica() {
        // Obtener fecha y hora de la muestra
        LocalDateTime fechaHora = getFechaHoraMuestraSismica();

        // Inicializar valores de detalles de la muestra
        Double velocidadOnda = null;
        Double frecuenciaOnda = null;
        Double longitudOnda = null;

        // Recorrer detalles asociados para obtener valores específicos
        if (this.detalleMuestrasSismicas != null) {
            for (DetalleMuestraSismica detalle : this.detalleMuestrasSismicas) {
                if (detalle.sosDenominacionTipoDeDatoVelocidadOnda()) {
                    velocidadOnda = detalle.getValor();
                    System.out.println(velocidadOnda);
                } else if (detalle.sosDenominacionTipoDeDatoFrecuenciaOnda()) {
                    frecuenciaOnda = detalle.getValor();
                    System.out.println(frecuenciaOnda);
                } else if (detalle.sosDenominacionTipoDeDatoLongitudOnda()) {
                    longitudOnda = detalle.getValor();
                    System.out.println(longitudOnda);
                } else {
                    System.out.println("DENOMINACION: " + detalle.getTipoDeDato().getDenominacion());
                    System.out.println(detalle.sosDenominacionTipoDeDatoLongitudOnda());
                }
            }
        }

        // Retornar información empaquetada como strings
        return prepararInformacionMuestraSismica(fechaHora, velocidadOnda, frecuenciaOnda, longitudOnda);
    }

    /**
     * Prepara la información de la muestra en formato ArrayList<String>.
     * Convierte los valores a strings y los empaqueta.
     */
    private ArrayList<String> prepararInformacionMuestraSismica(
            LocalDateTime fechaHora, Double velocidadOnda, Double frecuenciaOnda, Double longitudOnda) {

        ArrayList<String> informacionMuestraSismica = new ArrayList<>();

        // Agregar fecha/hora
        informacionMuestraSismica.add(fechaHora != null ? fechaHora.toString() : "");

        // Agregar valores de detalles (con valores por defecto si son null)
        informacionMuestraSismica.add(velocidadOnda != null ? String.valueOf(velocidadOnda) : "0.0");
        informacionMuestraSismica.add(frecuenciaOnda != null ? String.valueOf(frecuenciaOnda) : "0.0");
        informacionMuestraSismica.add(longitudOnda != null ? String.valueOf(longitudOnda) : "0.0");

        return informacionMuestraSismica;
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
