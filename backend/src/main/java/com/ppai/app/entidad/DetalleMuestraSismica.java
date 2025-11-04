package com.ppai.app.entidad;

public class DetalleMuestraSismica {
 
    // Atributos
    private long idDetalleMuestraSismica;
    private long idTipoDeDato;
    private double valor;

    // Comportamiento
    public String getDatos(){
        // Implementar la logica del método para
        return "falta implementar este método";
    }

    // Métodos Getter y Setter
    public long getIdDetalleMuestraSismica(){
        return this.idDetalleMuestraSismica;
    }
    public long getIdTipoDeDato(){
        return this.idTipoDeDato;
    } 
    public double getValor(){
        return this.valor;
    }
    public void setIdDetalleMuestraSismica(long idDetalleMuestraSismica) {
        this.idDetalleMuestraSismica = idDetalleMuestraSismica;
    }
    public void setIdTipoDeDato(long idTipoDeDato) {
        this.idTipoDeDato = idTipoDeDato;
    }
    public void setValor(double valor){
        this.valor = valor;
    }

}
