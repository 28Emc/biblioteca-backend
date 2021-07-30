package com.biblioteca.backend.model;

import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_empleado")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(name = "id_usuario")
    @ApiModelProperty(notes = "ID del usuario relacionado con el empleado", required = true, example = "1")
    private Long idUsuario;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_local")
    private Local local;

    @Column(name = "fecha_registro", nullable = false)
    @ApiModelProperty(notes = "Fecha de creación del empleado", required = true, example = "2020-05-25")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    @ApiModelProperty(notes = "Fecha de actualización del empleado", example = "2020-06-01")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    @ApiModelProperty(notes = "Fecha de baja del empleado", example = "2020-06-01")
    private LocalDateTime fechaBaja;

    // EMPLEADO(1):PRESTAMO(*)
    //@JsonIgnore
    @OneToMany(mappedBy = "empleado"/*, fetch = FetchType.LAZY, cascade = CascadeType.ALL*/)
    private List<Prestamo> prestamos;

    public Empleado() {
    }

    public Empleado(Long idUsuario, Local local) {
        this.idUsuario = idUsuario;
        this.local = local;
    }

    public Empleado(Long id, Long idUsuario, Local local, LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion, LocalDateTime fechaBaja) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.local = local;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaBaja = fechaBaja;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
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

    public LocalDateTime getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDateTime fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
