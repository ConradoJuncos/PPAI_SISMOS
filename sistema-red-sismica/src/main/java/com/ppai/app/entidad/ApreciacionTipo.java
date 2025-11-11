package com.ppai.app.entidad;

public class ApreciacionTipo {

    // Atributos 
    private long idApreciacionTipo;
    private String color;
    private String leyenda;

    // Métodos Getter y Setter
    public long getIdApreciacionTipo(){
        return this.idApreciacionTipo;
    }
    public String getColor(){
        return this.color;
    }
    public String getLeyenda(){
        return this.leyenda;
    }
    public void setIdApreciacionTipo(long idApreciacionTipo){
        this.idApreciacionTipo = idApreciacionTipo;
    }
    public void setColor(String color){
        this.color = color;
    }
    public void setLeyenda(String leyenda){
        this.leyenda = leyenda;
    }
    
}
