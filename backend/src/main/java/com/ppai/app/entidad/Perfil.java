package com.ppai.app.entidad;

import java.util.ArrayList;
import java.util.List;

public class Perfil {

    // Atributos
    private long idPerfil;
    private String descripcion;
    private String nombre;
    private List<Permiso> permisos = new ArrayList<Permiso>();
    
    // Métodos Getter y Setter
    public long getIdPerfil(){
        return this.idPerfil;
    }
    public String getDescripcion(){
        return this.descripcion;
    }
    public String getNombre(){
        return this.nombre;
    }
    public List<Permiso> getPermiso(){
        return this.permisos;
    }
    public void setIdPerfil(long idPerfil){
        this.idPerfil = idPerfil;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setPermisos(List<Permiso> permisos){
        this.permisos = permisos;
    }
    
}
