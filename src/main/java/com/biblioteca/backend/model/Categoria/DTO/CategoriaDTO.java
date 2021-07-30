package com.biblioteca.backend.model.Categoria.DTO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CategoriaDTO {

    @NotEmpty(message = "{notEmpty.categoriaDTO.nombre}")
    @Size(min = 1, max = 60, message = "{size.categoriaDTO.nombre}")
    private String nombre;

    private String descripcion;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public CategoriaDTO() {
    }

    public CategoriaDTO(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
