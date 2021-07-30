package com.biblioteca.backend.model.Persona;

import com.biblioteca.backend.model.Usuario.Usuario;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_persona")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @ApiModelProperty(notes = "Nombre de la persona", required = true, example = "pepito")
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false)
    @ApiModelProperty(notes = "Apellido paterno de la persona", required = true, example = "paredes")
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false)
    @ApiModelProperty(notes = "Apellido materno de la persona", required = true, example = "rojas")
    private String apellidoMaterno;

    @Column(name = "tipo_documento", nullable = false)
    @ApiModelProperty(notes = "Tipo de documento de identidad", required = true, example = "DNI")
    private String tipoDocumento;

    @Column(name = "nro_documento", nullable = false)
    @ApiModelProperty(notes = "Número del documento de identidad", required = true, example = "47111025")
    private String nroDocumento;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Sexo de la persona", required = true, example = "M")
    private String sexo;

    @ApiModelProperty(notes = "Dirección de la persona", example = "Av. Lima 123")
    private String direccion;

    @ApiModelProperty(notes = "Número de celular de la persona", example = "983489303")
    private String celular;

    @Column(name = "fecha_registro", nullable = false)
    @ApiModelProperty(notes = "Fecha de registro de la persona", required = true, example = "2021-07-27T18:10:09")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    @ApiModelProperty(notes = "Fecha de actualización de la persona", example = "2021-07-27T18:10:09")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    @ApiModelProperty(notes = "Fecha de baja de la persona", example = "2021-07-27T18:10:09")
    private LocalDateTime fechaBaja;

    //@JsonIgnore
    @OneToMany(mappedBy = "persona")
    private List<Usuario> usuarios;

    public Persona() {
    }

    public Persona(Long id, String nombre, String apellidoPaterno, String apellidoMaterno, String tipoDocumento, String nroDocumento, String sexo, String direccion, String celular, LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion, LocalDateTime fechaBaja) {
        this.id = id;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.tipoDocumento = tipoDocumento;
        this.nroDocumento = nroDocumento;
        this.sexo = sexo;
        this.direccion = direccion;
        this.celular = celular;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
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

    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
