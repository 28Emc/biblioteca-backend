package com.biblioteca.backend.model.Prestamo.DTO;

import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Usuario.Usuario;
import lombok.Data;

import java.util.Date;

@Data
public class PrestamoDTO {

    private Long id;

    private Date fechaDevolucion;

    private boolean isActivo;

    private String observaciones;

    private Usuario usuario;

    private Usuario empleado;

    private Libro libro;
}
