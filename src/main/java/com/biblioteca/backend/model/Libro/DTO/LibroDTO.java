package com.biblioteca.backend.model.Libro.DTO;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class LibroDTO {

    private Long id;

    @Column(name = "id_categoria")
    @NotNull(message = "{notNull.libroDTO.idCategoria}")
    private Long idCategoria;

    @Column(name = "id_local")
    @NotNull(message = "{notNull.libroDTO.idLocal}")
    private Long idLocal;

    @NotEmpty(message = "{notEmpty.libroDTO.isbn}")
    @Size(min = 1, max = 30, message = "{size.libroDTO.isbn}")
    @Column(name = "isbn")
    private String ISBN;

    @NotEmpty(message = "{notEmpty.libroDTO.titulo}")
    @Size(min = 1, max = 255, message = "{size.libroDTO.titulo}")
    private String titulo;

    @NotEmpty(message = "{notEmpty.libroDTO.autor}")
    @Size(min = 1, max = 100, message = "{size.libroDTO.autor}")
    private String autor;

    private String descripcion;

    @Min(value = 1)
    @Max(value = 99999)
    @NotNull(message = "{notNull.libroDTO.stock}")
    private Integer stock;

    @Size(min = 1, max = 255, message = "{size.libroDTO.foto}")
    @Column(name = "foto_libro")
    private String fotoLibro;

    @NotNull(message = "{notNull.libroDTO.fechaPublicacion}")
    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Long getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Long idLocal) {
        this.idLocal = idLocal;
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

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
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

    public LibroDTO() {
    }

    public LibroDTO(Long idCategoria, Long idLocal, String ISBN, String titulo, String autor, String descripcion, Integer stock, String fotoLibro, LocalDateTime fechaPublicacion) {
        this.idCategoria = idCategoria;
        this.idLocal = idLocal;
        this.ISBN = ISBN;
        this.titulo = titulo;
        this.autor = autor;
        this.descripcion = descripcion;
        this.stock = stock;
        this.fotoLibro = fotoLibro;
        this.fechaPublicacion = fechaPublicacion;
    }

    public LibroDTO(Long id, Long idCategoria, Long idLocal, String ISBN, String titulo, String autor, String descripcion, Integer stock, String fotoLibro, LocalDateTime fechaPublicacion) {
        this.id = id;
        this.idCategoria = idCategoria;
        this.idLocal = idLocal;
        this.ISBN = ISBN;
        this.titulo = titulo;
        this.autor = autor;
        this.descripcion = descripcion;
        this.stock = stock;
        this.fotoLibro = fotoLibro;
        this.fechaPublicacion = fechaPublicacion;
    }
}
