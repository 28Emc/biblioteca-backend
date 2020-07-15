package com.biblioteca.backend.model.Categoria.DTO;

public class CategoriaDTO {

    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaDTO() {
    }

    public CategoriaDTO(String nombre) {
        this.nombre = nombre;
    }
}
