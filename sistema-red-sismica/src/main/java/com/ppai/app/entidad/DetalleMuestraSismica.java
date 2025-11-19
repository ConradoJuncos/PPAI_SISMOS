package com.ppai.app.entidad;

public class DetalleMuestraSismica {
 
    // Atributos
    private long idDetalleMuestraSismica;
    private TipoDeDato tipoDeDato;
    private double valor;

    // Constructor sin parametros
    public DetalleMuestraSismica(){}

    public boolean sosDenominacionTipoDeDatoVelocidadOnda() {
        return this.tipoDeDato.sosDenominacionVelocidadOnda();
    }

    public boolean sosDenominacionTipoDeDatoFrecuenciaOnda() {
        return this.tipoDeDato.sosDenominacionFrecuenciaOnda();
    }

    public boolean sosDenominacionTipoDeDatoLongitudOnda() {
        return this.tipoDeDato.sosDenominacionLongitudOnda();
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
