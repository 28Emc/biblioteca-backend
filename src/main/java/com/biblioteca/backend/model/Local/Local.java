package com.biblioteca.backend.model.Local;

import com.biblioteca.backend.model.Empresa;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_local")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(nullable = false, unique = true)
    @ApiModelProperty(notes = "Dirección del local", required = true, example = "Av. Lima 123")
    private String direccion;

    @Column(name = "info_adicional")
    @ApiModelProperty(notes = "Información adicional del local", example = "Local central")
    private String infoAdicional;

    @Column(name = "fecha_registro", nullable = false)
    @ApiModelProperty(notes = "Fecha de creación del local", required = true, example = "2020-05-25")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    @ApiModelProperty(notes = "Fecha de actualización del local", example = "2020-06-01")
    private LocalDateTime fechaActualizacion;

    @Column(name = "is_activo", nullable = false)
    @ApiModelProperty(notes = "Estado del local", required = true, example = "true")
    private boolean isActivo;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    // LOCAL(1):EMPLEADO(*)
    //@JsonIgnore
    @OneToMany(mappedBy = "local")
    private List<Usuario> usuarios;

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

    public String getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(String infoAdicional) {
        this.infoAdicional = infoAdicional;
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

    public boolean isActivo() {
        return isActivo;
    }

    public void setActivo(boolean activo) {
        isActivo = activo;
    }

    @JsonBackReference
    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @JsonManagedReference
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Local() {
    }

    public Local(Long id, String direccion, String infoAdicional, LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion, boolean isActivo, Empresa empresa, List<Usuario> usuarios) {
        this.id = id;
        this.direccion = direccion;
        this.infoAdicional = infoAdicional;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
        this.isActivo = isActivo;
        this.empresa = empresa;
        this.usuarios = usuarios;
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