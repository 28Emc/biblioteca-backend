package com.biblioteca.backend.model.Local;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_local")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(name = "id_empresa")
    @ApiModelProperty(notes = "ID de la empresa relacionada con el local", required = true, example = "1")
    private Long idEmpresa;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Dirección del local", required = true, example = "Av. Lima 123")
    private String direccion;

    @Column(name = "fecha_registro", nullable = false)
    @ApiModelProperty(notes = "Fecha de creación del local", required = true, example = "2020-05-25")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    @ApiModelProperty(notes = "Fecha de actualización del local", example = "2020-06-01")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    @ApiModelProperty(notes = "Fecha de baja del local", example = "2020-06-01")
    private LocalDateTime fechaBaja;

    @Column(name = "is_activo", nullable = false)
    @ApiModelProperty(notes = "Estado del local", required = true, example = "true")
    private boolean isActivo;

    // LOCAL(1):EMPLEADO(*)
    //@JsonIgnore
    //@OneToMany(mappedBy = "local")
    //private List<Usuario> usuarios;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public boolean isActivo() {
        return isActivo;
    }

    public void setActivo(boolean activo) {
        isActivo = activo;
    }

    @JsonBackReference
    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    /*
    @JsonManagedReference
    /public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
    */

    public Local() {
    }

    public Local(Long id, Long idEmpresa, String direccion, LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion, LocalDateTime fechaBaja, boolean isActivo) {
        this.id = id;
        this.idEmpresa = idEmpresa;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaBaja = fechaBaja;
        this.isActivo = isActivo;
    }

    @PrePersist
    public void prePersist() {
        isActivo = true;
        fechaRegistro = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

}