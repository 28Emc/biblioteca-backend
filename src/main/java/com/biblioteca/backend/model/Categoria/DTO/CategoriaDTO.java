package com.biblioteca.backend.model.Categoria.DTO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CategoriaDTO {

    @NotEmpty(message = "{notEmpty.categoriaDTO.nombre}")
    @Size(min = 4, max = 30, message = "{size.categoriaDTO.nombre}")
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
