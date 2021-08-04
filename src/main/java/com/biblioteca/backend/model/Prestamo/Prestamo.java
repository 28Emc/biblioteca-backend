package com.biblioteca.backend.model.Prestamo;

import com.biblioteca.backend.model.Empleado.Empleado;
import com.biblioteca.backend.model.Libro.Libro;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Type;

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

    // PRESTAMOS(*):USER(1) - RELACIÓN INDIRECTA YA QUE PERTENECEN A DISTINTAS BD
    @Column(name = "id_usuario")
    private Long idUsuario;

    // PRESTAMOS(*):EMPLEADO(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    // PRESTAMOS(*):LIBRO(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_libro")
    private Libro libro;

    @Column(name = "fecha_registro", nullable = false)
    @ApiModelProperty(notes = "Fecha de registro del préstamo", required = true, example = "2020-03-12")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_prestamo")
    @ApiModelProperty(notes = "Fecha de préstamo", example = "2020-03-12")
    private LocalDateTime fechaPrestamo;

    @Column(name = "fecha_devolucion")
    @ApiModelProperty(notes = "Fecha de devolución del préstamo", example = "2020-03-12")
    private LocalDateTime fechaDevolucion;

    @Column(name = "fecha_baja")
    @ApiModelProperty(notes = "Fecha de baja del préstamo", example = "2020-03-12")
    private LocalDateTime fechaBaja;

    @Type(type = "text")
    @ApiModelProperty(notes = "Observaciones del préstamo", example = "El préstamo del libro A ha sido anulado por el empleado B el dia C")
    private String observaciones;

    @Column(name = "estado", nullable = false)
    @ApiModelProperty(notes = "Estado del préstamo", required = true, example = "E1")
    private String estado;

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    //@JsonBackReference
    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
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

    public Prestamo(Long id, Long idUsuario, Empleado empleado, Libro libro, LocalDateTime fechaRegistro, LocalDateTime fechaPrestamo, LocalDateTime fechaDevolucion, LocalDateTime fechaBaja, String observaciones, String estado) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.empleado = empleado;
        this.libro = libro;
        this.fechaRegistro = fechaRegistro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.fechaBaja = fechaBaja;
        this.observaciones = observaciones;
        this.estado = estado;
    }

    @PrePersist
    public void prePersist() {
        estado = "E1";
        fechaRegistro = LocalDateTime.now();
    }

}