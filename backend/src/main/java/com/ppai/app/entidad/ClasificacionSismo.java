package com.ppai.app.entidad;

public class ClasificacionSismo {
    
    // Atributos
    private long idClasificacionSismo;
    private double kmProfundidadDesde;
    private double kmProfundidadHasta;
    private String nombre;

    // Métodos Getter y Setter
    public long getIdClasificacionSismo(){
        return this.idClasificacionSismo;
    }
    public String getNombre(){
        return this.nombre;
    }
    public double getkmProfundidadDesde(){
        return this.kmProfundidadDesde;
    }
    public double getKmProfundidadHasta(){
        return this.kmProfundidadHasta;
    }
    public void setIdClasificacionSismo(long idClasificacionSismo){
        this.idClasificacionSismo = idClasificacionSismo;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setKmProfundidadDesde(double kmProfundidadDesde){
        this.kmProfundidadDesde = kmProfundidadDesde;
    }
    public void setKmProfundidadHasta(double kmProfundidadHasta){
        this.kmProfundidadHasta = kmProfundidadHasta;
    }

}
