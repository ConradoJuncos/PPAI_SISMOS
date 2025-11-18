package com.ppai.app.entidad;

public class Empleado {

    private long idEmpleado;
    private String apellido;
    private String mail;
    private String nombre;
    private String telefono;

    public Empleado() {}

    public long getIdEmpleado(){ 
        return this.idEmpleado; 
    }
    public String getApellido(){ 
        return this.apellido; 
    }
    public String getMail(){ 
        return this.mail; 
    }
    public String getNombre(){
        return this.nombre;
    }
    public String getTelefono() { 
        return this.telefono; 
    }
    public void setIdEmpleado(long idEmpleado){
        this.idEmpleado = idEmpleado;
    }
    public void setApellido(String apellido){
        this.apellido = apellido;
    }
    public void setMail(String mail){
        this.mail = mail;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setTelefono(String telefono){
        this.telefono = telefono;
    }

    
}
