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

    @Column(unique = true, nullable = false)
    @ApiModelProperty(notes = "Nombre de la categoría", required = true, example = "Fantasía")
    private String nombre;

    @Column(name = "fecha_registro", nullable = false)
    @ApiModelProperty(notes = "Fecha de creación de la categoría", required = true, example = "2020-05-25")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    @ApiModelProperty(notes = "Fecha de actualización de la categoría", example = "2020-06-01")
    private LocalDateTime fechaActualizacion;

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

    public Categoria(Long id, String nombre, LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion, boolean isActivo, List<Libro> libros) {
        this.id = id;
        this.nombre = nombre;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
        this.isActivo = isActivo;
        this.libros = libros;
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