package com.ppai.app.DTO;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class SerieTemporalDTO {
    
    // Atributos 
    private String nombreEstacion; // atributo para la informacion sismica
    private long codigoEstacion; // atributo para la inforamcion sismica
    private long idSerieTemporal;
    private LocalDateTime fechaHoraRegistro;
    private double frecuenciaMuestreo;
    private List<MuestraSismicaDTO> muestras = new ArrayList<MuestraSismicaDTO>();
}
