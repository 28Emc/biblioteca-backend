package com.biblioteca.backend.model.Prestamo.DTO;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class PrestamoDTO {

    private Long id;

    @Column(name = "id_usuario")
    @NotNull(message = "{notNull.prestamoDTO.idUsuario}")
    private Long idUsuario;

    @Column(name = "id_empleado")
    @NotNull(message = "{notNull.prestamoDTO.idEmpresa}")
    private Long idEmpleado;

    @Column(name = "id_libro")
    @NotNull(message = "{notNull.prestamoDTO.idLibro}")
    private Long idLibro;

    @NotNull(message = "{notNull.prestamoDTO.fechaDevolucion}")
    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;

    private String observaciones;

    private String estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Long idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Long getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Long idLibro) {
        this.idLibro = idLibro;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public PrestamoDTO() {
    }

    public PrestamoDTO(Long idUsuario, Long idEmpleado, Long idLibro, LocalDateTime fechaDevolucion, String observaciones, String estado) {
        this.idUsuario = idUsuario;
        this.idEmpleado = idEmpleado;
        this.idLibro = idLibro;
        this.fechaDevolucion = fechaDevolucion;
        this.observaciones = observaciones;
        this.estado = estado;
    }

    public PrestamoDTO(Long id, Long idUsuario, Long idEmpleado, Long idLibro, LocalDateTime fechaDevolucion, String observaciones, String estado) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idEmpleado = idEmpleado;
        this.idLibro = idLibro;
        this.fechaDevolucion = fechaDevolucion;
        this.observaciones = observaciones;
        this.estado = estado;
    }
}
