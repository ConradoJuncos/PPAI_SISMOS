package com.ppai.app.entidad;

public class TipoDeDato {
    
    // Atributos
    private long idTipoDeDato;
    private String denominacion;
    private String nombreUnidadMedida;
    private double valorUmbral;

    // Comportamiento
    public boolean esTuDenominacion(String denominacion){
        return this.denominacion == denominacion;
    }

    // Métodos Getter y Setter
    public long getIdTipoDeDato(){
        return this.idTipoDeDato;
    }
    public String getDenominacion(){
        return this.denominacion;
    }
    public String getnombreUnidadMedida(){
        return this.nombreUnidadMedida;
    }
    public double getValorUmbral(){
        return this.valorUmbral;
    }
    public void setIdTipoDeDato(long idTipoDeDato){
        this.idTipoDeDato = idTipoDeDato;
    }
    public void setDenominacion(String denominacion){
        this.denominacion = denominacion;
    }
    public void setnombreUnidadMedida(String nombreUnidadMedida){
        this.nombreUnidadMedida = nombreUnidadMedida;
    }
    public void setValorUmbral(double valorUmbral){
        this.valorUmbral = valorUmbral;
    }
}

