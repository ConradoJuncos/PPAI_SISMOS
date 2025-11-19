package com.ppai.app.entidad;

public class Usuario {

    // Atributos
    private long idUsuario;
    private String contraseña;
    private String nombreUsuario;
    private Empleado empleado;

    // Metodo Constructor con parametros
    public Usuario(long idUsuario, String contraseña, String nombre){
        setIdUsuario(idUsuario);
        setContraseña(contraseña);
        setNombreUsuario(nombre);
    }

    // Método Constructor sin parámetros
    public Usuario(){}

    // Comportamiento
    public Empleado obtenerEmpleado(){
        return getEmpleado();
    }

    // Métodos Getter y Setter
    public long getIdUsuario(){
        return this.idUsuario;
    }
    public String getContraseña(){
        return this.contraseña;
    }
    public String getNombreUsuario(){
        return this.nombreUsuario;
    }
    public Empleado getEmpleado(){
        return this.empleado;
    }
    public void setIdUsuario(long idUsuario){
        this.idUsuario = idUsuario;
    }
    public void setContraseña(String contraseña){
        this.contraseña = contraseña;
    }
    public void setNombreUsuario(String nombreUsuario){
        this.nombreUsuario = nombreUsuario;
    }
    public void setEmpleado(Empleado empleado){
        this.empleado = empleado;
    }
}
