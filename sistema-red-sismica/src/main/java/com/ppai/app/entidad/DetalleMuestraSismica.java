package com.ppai.app.entidad;

import com.ppai.app.entidad.TipoDeDato;

public class DetalleMuestraSismica {
 
    // Atributos
    private long idDetalleMuestraSismica;
    private TipoDeDato tipoDeDato;
    private double valor;

    // Constructor sin parametros
    public DetalleMuestraSismica(){}

    // Comportamiento
    public String getDatos(){
        // Implementar la logica del método para
        return "falta implementar este método";
    }

    // Métodos Getter y Setter
    public long getIdDetalleMuestraSismica(){
        return this.idDetalleMuestraSismica;
    }
    public TipoDeDato getTipoDeDato(){
        return this.tipoDeDato;
    } 
    public double getValor(){
        return this.valor;
    }
    public void setIdDetalleMuestraSismica(long idDetalleMuestraSismica) {
        this.idDetalleMuestraSismica = idDetalleMuestraSismica;
    }
    public void setTipoDeDato(TipoDeDato tipoDeDato) {
        this.tipoDeDato = tipoDeDato;
    }
    public void setValor(double valor){
        this.valor = valor;
    }

}
