package com.biblioteca.backend.model;

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
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_prestamos")
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

    @Column(name = "fecha_registro", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(notes = "Fecha de devolución del préstamo", required = true, example = "2020-03-12")
    private Date fechaDevolucion;

    @Column(name = "estado", nullable = false)
    @ApiModelProperty(notes = "Estado del préstamo", required = true, example = "true")
    private boolean isActivo;

    @Column(length = 255, nullable = false)
    @ApiModelProperty(notes = "Observaciones del préstamo", required = true, example = "El préstamo del libro A ha sido anulado por el empleado B el dia C")
    private String observaciones;

    // PRESTAMOS(*):USER(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // PRESTAMOS(*):EMPLEADO(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Usuario empleado;

    // PRESTAMOS(*):LIBRO(1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @PrePersist
    public void prePersist() {
        fechaDespacho = new Date();
    }

}