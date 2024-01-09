package com.biblioteca.backend.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @ApiModelProperty(notes = "ID Autogenerado")
    private Long id;

    @Column(unique = true, nullable = false)
    // @ApiModelProperty(notes = "Nombre de la categoría", required = true, example = "Fantasía")
    private String name;

    @Column(name = "creation_date", nullable = false)
    // @ApiModelProperty(notes = "Fecha de creación de la categoría", required = true, example = "2020-05-25")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    // @ApiModelProperty(notes = "Fecha de actualización de la categoría", example = "2020-06-01")
    private LocalDateTime modificationDate;

    @Column(name = "status", nullable = false)
    // @ApiModelProperty(notes = "Estado de la categoría", required = true, example = "A")
    private String status;

    /*
    //@JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<Libro> libros;

    @JsonManagedReference
    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }
    */

    @PrePersist
    public void prePersist() {
        status = "A";
        creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        modificationDate = LocalDateTime.now();
    }
}