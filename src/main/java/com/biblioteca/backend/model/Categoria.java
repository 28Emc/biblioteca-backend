package com.biblioteca.backend.model;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.biblioteca.backend.model.Libro.Libro;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_categoria")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID Autogenerado")
    private Long id;

    @Column(unique = true, nullable = false)
    @ApiModelProperty(notes = "Nombre de la categoría", required = true, example = "Fantasía")
    private String nombre;

    @Column(name = "fecha_registro", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(notes = "Fecha de creación de la categoría", required = true, example = "2020-05-25")
    private Date fechaRegistro;

    @Column(name = "fecha_actualizacion", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(notes = "Fecha de actualización de la categoría", required = false, example = "2020-06-01")
    private Date fechaActualizacion;

    @Column(name = "is_activo", nullable = false)
    @ApiModelProperty(notes = "Estado de la categoría", required = true, example = "true")
    private boolean isActivo;

    @JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<Libro> libros;

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