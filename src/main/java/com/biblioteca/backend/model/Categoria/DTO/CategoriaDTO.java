package com.biblioteca.backend.model.Categoria.DTO;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CategoriaDTO {

    /*@NotEmpty(message = "El nombre de la categoría es requerido")
    @Size(min = 4, max = 30)
    @Column(unique = true)*/
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
