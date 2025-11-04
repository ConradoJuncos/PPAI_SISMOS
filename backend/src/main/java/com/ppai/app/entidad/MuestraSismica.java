package com.ppai.app.entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MuestraSismica {
    
    // Atributos
    private long idMuestraSismica;
    private LocalDateTime fechaHoraMuestraSismica;
    private List<Long> idDetalleMuestraSismica = new ArrayList<>();

    // Comportamiento
    public String getDatos(){
        // Implementar la lógica de este metodo
        return "El método para mostrar los datos de muestra sismica todavía no ha sido implementado.";
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
    public List<Long> getIdDetalleMuestraSismica(){
        return this.idDetalleMuestraSismica;
    }
    public void setIdMuestraSismica(long idMuestraSismica) {
        this.idMuestraSismica = idMuestraSismica;
    }
    public void setFechaHoraMuestraSismica(LocalDateTime fechaHoraMuestraSismica){
        this.fechaHoraMuestraSismica = fechaHoraMuestraSismica;
    }
    public void setIdDetalleMuestraSismica(List<Long> idDetalleMuestraSismica){
        this.idDetalleMuestraSismica = idDetalleMuestraSismica;
    }
}
