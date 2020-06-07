package com.biblioteca.backend.model;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_libros")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID Autogenerado")
    private Long id;

    @Column(length = 100, nullable = false)
    @ApiModelProperty(notes = "Titulo del libro", required = true, example = "El Camino de los Reyes")
    private String titulo;

    @Column(length = 100, nullable = false)
    @ApiModelProperty(notes = "Autor del libro", required = true, example = "Brandon Sanderson")
    private String autor;

    @Type(type = "text")
    @Column(nullable = true)
    @ApiModelProperty(notes = "Descripción del libro", required = false, example = "Libro que hace parte de una trilogía")
    private String descripcion;

    @Transient
    private String descripcionMin;

    @Column(name = "fecha_publicacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(notes = "Fecha de publicación del libro", required = true, example = "2020-05-25")
    private Date fechaPublicacion;

    @Column(name = "fecha_registro", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(notes = "Fecha de creación del libro", required = true, example = "2020-05-25")
    private Date fechaRegistro;

    @Column(name = "fecha_actualizacion", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(notes = "Fecha de actualización del libro", required = false, example = "2020-06-01")
    private Date fechaActualizacion;

    @Column(name = "estado", nullable = false)
    @ApiModelProperty(notes = "Estado del libro", required = true, example = "true")
    private boolean isActivo;

    @Column(length = 4, nullable = false)
    @ApiModelProperty(notes = "Stock del libro", required = true, example = "500")
    private Integer stock;

    @Column(name = "foto_libro", nullable = false)
    @ApiModelProperty(notes = "Foto portada del libro", required = true, example = "el-camino-de-los-reyes.png")
    private String fotoLibro;

    // LIBRO(*):LOCAL(1)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id")
    private Local local;

    // LIBRO(1):PRESTAMO(*)
    @JsonIgnore
    @OneToMany(mappedBy = "libro"
    , fetch = FetchType.LAZY, cascade = CascadeType.ALL
    )
    private List<Prestamo> prestamos;

    // LIBRO(*):CATEGORIA(1)
    // @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @PrePersist
    public void prePersist() {
        isActivo = true;
        fechaRegistro = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = new Date();
    }

}