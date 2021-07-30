package com.biblioteca.backend.model.Categoria;

import com.biblioteca.backend.model.Libro.Libro;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_categoria")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID Autogenerado")
    private Long id;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Nombre de la categoría", required = true, example = "Fantasía")
    private String nombre;

    @ApiModelProperty(notes = "Descripción de la categoría", example = "Libros de fantasía")
    private String descripcion;

    @Column(name = "fecha_registro", nullable = false)
    @ApiModelProperty(notes = "Fecha de creación de la categoría", required = true, example = "2020-05-25")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    @ApiModelProperty(notes = "Fecha de actualización de la categoría", example = "2020-06-01")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    @ApiModelProperty(notes = "Fecha de baja de la categoría", example = "2020-06-01")
    private LocalDateTime fechaBaja;

    @Column(name = "is_activo", nullable = false)
    @ApiModelProperty(notes = "Estado de la categoría", required = true, example = "true")
    private boolean isActivo;

    //@JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<Libro> libros;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
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

    @JsonManagedReference
    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    public Categoria() {
    }

    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Categoria(Long id, String nombre, String descripcion, LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion, LocalDateTime fechaBaja, boolean isActivo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaBaja = fechaBaja;
        this.isActivo = isActivo;
    }

    @PrePersist
    public void prePersist() {
        isActivo = true;
        fechaRegistro = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

}