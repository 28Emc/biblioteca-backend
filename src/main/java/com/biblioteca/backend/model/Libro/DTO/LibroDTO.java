package com.biblioteca.backend.model.Libro.DTO;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class LibroDTO {

    private Long id;

    @NotEmpty(message = "{notEmpty.libroDTO.isbn}")
    private String ISBN;

    @NotEmpty(message = "{notEmpty.libroDTO.titulo}")
    @Size(min = 1, max = 100, message = "{size.libroDTO.titulo}")
    private String titulo;

    @NotEmpty(message = "{notEmpty.libroDTO.autor}")
    @Size(min = 1, max = 100, message = "{size.libroDTO.autor}")
    private String autor;

    private String descripcion;

    @NotNull(message = "{notNull.libroDTO.fechaPublicacion}")
    private LocalDateTime fechaPublicacion;

    private boolean isActivo;

    @Min(value = 1)
    @Max(value = 9999)
    @NotNull(message = "{notNull.libroDTO.stock}")
    private Integer stock;

    private String fotoLibro;

    private Long local;

    private Long categoria;

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

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
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

    public Long getLocal() {
        return local;
    }

    public void setLocal(Long local) {
        this.local = local;
    }

    public Long getCategoria() {
        return categoria;
    }

    public void setCategoria(Long categoria) {
        this.categoria = categoria;
    }

    public LibroDTO() {
    }

    public LibroDTO(Long id, String ISBN, String titulo, String autor, String descripcion, LocalDateTime fechaPublicacion, boolean isActivo, Integer stock, String fotoLibro, Long local, Long categoria) {
        this.id = id;
        this.ISBN = ISBN;
        this.titulo = titulo;
        this.autor = autor;
        this.descripcion = descripcion;
        this.fechaPublicacion = fechaPublicacion;
        this.isActivo = isActivo;
        this.stock = stock;
        this.fotoLibro = fotoLibro;
        this.local = local;
        this.categoria = categoria;
    }
}
