package com.biblioteca.backend.model.Prestamo.DTO;

import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Usuario.Usuario;

import java.time.LocalDateTime;

public class PrestamoDTO {

    private Long id;

    private LocalDateTime fechaDevolucion;

    private boolean isActivo;

    private String observaciones;

    private Long usuario;

    private Long empleado;

    private Long libro;

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

    public Long getUsuario() {
        return usuario;
    }

    public void setUsuario(Long usuario) {
        this.usuario = usuario;
    }

    public Long getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Long empleado) {
        this.empleado = empleado;
    }

    public Long getLibro() {
        return libro;
    }

    public void setLibro(Long libro) {
        this.libro = libro;
    }

    public PrestamoDTO() {
    }

    public PrestamoDTO(Long id, LocalDateTime fechaDevolucion, boolean isActivo, String observaciones, Long usuario, Long empleado, Long libro) {
        this.id = id;
        this.fechaDevolucion = fechaDevolucion;
        this.isActivo = isActivo;
        this.observaciones = observaciones;
        this.usuario = usuario;
        this.empleado = empleado;
        this.libro = libro;
    }
}
