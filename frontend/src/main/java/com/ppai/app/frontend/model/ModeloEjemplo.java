package com.ppai.app.frontend.model;

/**
 * Modelo de datos de ejemplo para el frontend.
 * Crea aquí las clases que representen los datos que manejarás en la interfaz.
 */
public class ModeloEjemplo {

    private Long id;
    private String nombre;

    public ModeloEjemplo() {
    }

    public ModeloEjemplo(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

