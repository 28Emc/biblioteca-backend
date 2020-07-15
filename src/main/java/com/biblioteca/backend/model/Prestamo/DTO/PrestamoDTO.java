package com.biblioteca.backend.model.Prestamo.DTO;

import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Usuario.Usuario;

import java.time.LocalDateTime;

public class PrestamoDTO {

    private Long id;

    private LocalDateTime fechaDevolucion;

    private boolean isActivo;

    private String observaciones;

    private Usuario usuario;

    private Usuario empleado;

    private Libro libro;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isActivo() {
        return isActivo;
    }

    public void setActivo(boolean activo) {
        isActivo = activo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Usuario empleado) {
        this.empleado = empleado;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public PrestamoDTO() {
    }

    public PrestamoDTO(Long id, LocalDateTime fechaDevolucion, boolean isActivo, String observaciones, Usuario usuario, Usuario empleado, Libro libro) {
        this.id = id;
        this.fechaDevolucion = fechaDevolucion;
        this.isActivo = isActivo;
        this.observaciones = observaciones;
        this.usuario = usuario;
        this.empleado = empleado;
        this.libro = libro;
    }
}
