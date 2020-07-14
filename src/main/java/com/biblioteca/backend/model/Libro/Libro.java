package com.biblioteca.backend.model.Libro;

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

import com.biblioteca.backend.model.Categoria;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.Type;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_libro")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_local")
    private Local local;

    // LIBRO(1):PRESTAMO(*)
    @JsonIgnore
    @OneToMany(mappedBy = "libro", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Prestamo> prestamos;

    // LIBRO(*):CATEGORIA(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @PrePersist
    public void prePersist() {
        isActivo = true;
        fechaRegistro = new Date();
        /*if (isISBN13(getISBN()))
            ISBN = getISBN();
        else ISBN = "";*/
    }

    // ALGORITMO PARA DETERMINAR SI EL CODIGO ISBN DEL LIBRO ES VÁLIDO
    public boolean isISBN13(String number) {
        int sum = 0;
        int multiple = 0;
        char ch = '\0';
        int digit = 0;
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
        if (sum % 10 == 0) {
            return true;
        } else {
            return false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = new Date();
    }

}