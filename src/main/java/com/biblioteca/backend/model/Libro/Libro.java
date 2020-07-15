package com.biblioteca.backend.model.Libro;

import com.biblioteca.backend.model.Categoria.Categoria;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID Autogenerado")
    private Long id;

    @Column(name = "isbn", nullable = false, unique = true)
    @ApiModelProperty(notes = "ISBN del libro", required = true, example = "9791234567896")
    private String ISBN;

    @Column(length = 100, nullable = false)
    @ApiModelProperty(notes = "Titulo del libro", required = true, example = "El Camino de los Reyes")
    private String titulo;

    @Column(length = 100, nullable = false)
    @ApiModelProperty(notes = "Autor del libro", required = true, example = "Brandon Sanderson")
    private String autor;

    @Type(type = "text")
    @Column
    @ApiModelProperty(notes = "Descripción del libro", example = "Libro que hace parte de una trilogía")
    private String descripcion;

    @Transient
    private String descripcionMin;

    @Column(name = "fecha_publicacion", nullable = false)
    @ApiModelProperty(notes = "Fecha de publicación del libro", required = true, example = "2020-05-25")
    private LocalDateTime fechaPublicacion;

    @Column(name = "fecha_registro", nullable = false)
    @ApiModelProperty(notes = "Fecha de creación del libro", required = true, example = "2020-05-25")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    @ApiModelProperty(notes = "Fecha de actualización del libro", example = "2020-06-01")
    private LocalDateTime fechaActualizacion;

    @Column(name = "is_activo", nullable = false)
    @ApiModelProperty(notes = "Estado del libro", required = true, example = "true")
    private boolean isActivo;

    @Column(length = 4, nullable = false)
    @ApiModelProperty(notes = "Stock del libro", required = true, example = "500")
    private Integer stock;

    @Column(name = "foto_libro", nullable = false)
    @ApiModelProperty(notes = "Foto portada del libro", required = true, example = "el-camino-de-los-reyes.png")
    private String fotoLibro;

    // LIBRO(*):LOCAL(1)
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id")
    private Local local;

    // LIBRO(1):PRESTAMO(*)
    //@JsonIgnore
    @OneToMany(mappedBy = "libro"/*, fetch = FetchType.LAZY, cascade = CascadeType.ALL*/)
    private List<Prestamo> prestamos;

    // LIBRO(*):CATEGORIA(1)
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcionMin() {
        return descripcionMin;
    }

    public void setDescripcionMin(String descripcionMin) {
        this.descripcionMin = descripcionMin;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getFotoLibro() {
        return fotoLibro;
    }

    public void setFotoLibro(String fotoLibro) {
        this.fotoLibro = fotoLibro;
    }

    @JsonBackReference
    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    @JsonManagedReference
    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    @JsonBackReference
    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Libro() {
    }

    public Libro(Long id, String ISBN, String titulo, String autor, String descripcion, String descripcionMin, LocalDateTime fechaPublicacion, LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion, boolean isActivo, Integer stock, String fotoLibro, Local local, List<Prestamo> prestamos, Categoria categoria) {
        this.id = id;
        this.ISBN = ISBN;
        this.titulo = titulo;
        this.autor = autor;
        this.descripcion = descripcion;
        this.descripcionMin = descripcionMin;
        this.fechaPublicacion = fechaPublicacion;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
        this.isActivo = isActivo;
        this.stock = stock;
        this.fotoLibro = fotoLibro;
        this.local = local;
        this.prestamos = prestamos;
        this.categoria = categoria;
    }

    @PrePersist
    public void prePersist() {
        isActivo = true;
        fechaRegistro = LocalDateTime.now();
        /*if (isISBN13(getISBN()))
            ISBN = getISBN();
        else ISBN = "";*/
    }

    // ALGORITMO PARA DETERMINAR SI EL CODIGO ISBN DEL LIBRO ES VÁLIDO
    public boolean isISBN13(String number) {
        int sum = 0;
        int multiple;
        char ch;
        int digit;
        for (int i = 1; i < 13; i++) {
            if (i % 2 == 0) {
                multiple = 3;
            } else {
                multiple = 1;
            }
            ch = number.charAt(i - 1);
            digit = Character.getNumericValue(ch);
            sum += (multiple * digit);
        }
        return sum % 10 == 0;
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

}