package com.biblioteca.backend.model.Prestamo;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_prestamo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID Autogenerado")
    private Long id;

    @Column(name = "fecha_despacho", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(notes = "Fecha de despacho del préstamo", required = true, example = "2020-03-12")
    private Date fechaDespacho;

    @Column(name = "fecha_devolucion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(notes = "Fecha de devolución del préstamo", required = true, example = "2020-03-12")
    private Date fechaDevolucion;

    @Column(name = "is_activo", nullable = false)
    @ApiModelProperty(notes = "Estado del préstamo", required = true, example = "true")
    private boolean isActivo;

    @Column(length = 255, nullable = false)
    @ApiModelProperty(notes = "Observaciones del préstamo", required = true, example = "El préstamo del libro A ha sido anulado por el empleado B el dia C")
    private String observaciones;

    // PRESTAMOS(*):USER(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // PRESTAMOS(*):EMPLEADO(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Usuario empleado;

    // PRESTAMOS(*):LIBRO(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro libro;

    @PrePersist
    public void prePersist() {
        isActivo = false;
        fechaDespacho = new Date();
    }

}