package com.biblioteca.backend.model.Prestamo;

import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_prestamo")
// PARA MOSTRAR EMPLEADOS, USUARIOS Y LIBROS DURANTE UNA CONSULTA
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID Autogenerado")
    private Long id;

    // PRESTAMOS(*):USER(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // PRESTAMOS(*):EMPLEADO(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_empleado")
    private Usuario empleado;

    // PRESTAMOS(*):LIBRO(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_libro")
    private Libro libro;

    @Column(name = "fecha_registro", nullable = false)
    @ApiModelProperty(notes = "Fecha de registro del préstamo", required = true, example = "2020-03-12")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_prestamo", nullable = false)
    @ApiModelProperty(notes = "Fecha de préstamo", required = true, example = "2020-03-12")
    private LocalDateTime fechaPrestamo;

    @Column(name = "fecha_devolucion", nullable = false)
    @ApiModelProperty(notes = "Fecha de devolución del préstamo", required = true, example = "2020-03-12")
    private LocalDateTime fechaDevolucion;

    @Column(name = "fecha_baja")
    @ApiModelProperty(notes = "Fecha de baja del préstamo", example = "2020-03-12")
    private LocalDateTime fechaBaja;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Observaciones del préstamo", required = true, example = "El préstamo del libro A ha sido anulado por el empleado B el dia C")
    private String observaciones;

    @Column(name = "is_activo", nullable = false)
    @ApiModelProperty(notes = "Estado del préstamo", required = true, example = "true")
    private boolean isActivo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDateTime fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public LocalDateTime getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDateTime fechaBaja) {
        this.fechaBaja = fechaBaja;
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

    //@JsonBackReference
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    //@JsonBackReference
    public Usuario getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Usuario empleado) {
        this.empleado = empleado;
    }

    //@JsonBackReference
    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Prestamo() {
    }

    public Prestamo(Long id, Usuario usuario, Usuario empleado, Libro libro, LocalDateTime fechaRegistro, LocalDateTime fechaPrestamo, LocalDateTime fechaDevolucion, LocalDateTime fechaBaja, String observaciones, boolean isActivo) {
        this.id = id;
        this.usuario = usuario;
        this.empleado = empleado;
        this.libro = libro;
        this.fechaRegistro = fechaRegistro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.fechaBaja = fechaBaja;
        this.observaciones = observaciones;
        this.isActivo = isActivo;
    }

    @PrePersist
    public void prePersist() {
        isActivo = false;
        fechaRegistro = LocalDateTime.now();
    }

}