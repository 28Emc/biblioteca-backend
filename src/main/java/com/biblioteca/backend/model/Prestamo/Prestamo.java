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

    @Column(name = "fecha_despacho", nullable = false)
    @ApiModelProperty(notes = "Fecha de despacho del préstamo", required = true, example = "2020-03-12")
    private LocalDateTime fechaDespacho;

    @Column(name = "fecha_devolucion", nullable = false)
    @ApiModelProperty(notes = "Fecha de devolución del préstamo", required = true, example = "2020-03-12")
    private LocalDateTime fechaDevolucion;

    @Column(name = "is_activo", nullable = false)
    @ApiModelProperty(notes = "Estado del préstamo", required = true, example = "true")
    private boolean isActivo;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Observaciones del préstamo", required = true, example = "El préstamo del libro A ha sido anulado por el empleado B el dia C")
    private String observaciones;

    // PRESTAMOS(*):USER(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // PRESTAMOS(*):EMPLEADO(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleado_id")
    private Usuario empleado;

    // PRESTAMOS(*):LIBRO(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "libro_id")
    private Libro libro;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(LocalDateTime fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
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

    public Prestamo(Long id, LocalDateTime fechaDespacho, LocalDateTime fechaDevolucion, boolean isActivo, String observaciones, Usuario usuario, Usuario empleado, Libro libro) {
        this.id = id;
        this.fechaDespacho = fechaDespacho;
        this.fechaDevolucion = fechaDevolucion;
        this.isActivo = isActivo;
        this.observaciones = observaciones;
        this.usuario = usuario;
        this.empleado = empleado;
        this.libro = libro;
    }

    @PrePersist
    public void prePersist() {
        isActivo = false;
        fechaDespacho = LocalDateTime.now();
    }

}