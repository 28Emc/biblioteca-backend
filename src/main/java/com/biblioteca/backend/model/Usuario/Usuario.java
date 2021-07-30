package com.biblioteca.backend.model.Usuario;

import com.biblioteca.backend.model.Persona.Persona;
import com.biblioteca.backend.model.Rol;
import com.biblioteca.backend.model.Token;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(name = "usuario", unique = true, nullable = false)
    @ApiModelProperty(notes = "Nombre del usuario para el inicio de sesión", required = true, example = "pepe@gmail.com")
    private String usuario;

    @Column(name = "password", nullable = false)
    @ApiModelProperty(notes = "Contraseña del usuario para el inicio de sesión", required = true, example = "$2a$10$mpnvIqpwTF6BJNlr4pXwOOCXk7KZiqZftFHt3IxwZ5ODYMfIBtHg6")
    private String password;

    @Transient
    @Column(name = "confirmar_password")
    @ApiModelProperty(notes = "Confirmar Contraseña del usuario", example = "$2a$10$mpnvIqpwTF6BJNlr4pXwOOCXk7KZiqZftFHt3IxwZ5ODYMfIBtHg6")
    private String passwordConfirmacion;

    @Column(name = "foto_usuario")
    @ApiModelProperty(notes = "Foto del usuario", example = "pepito2020.png")
    private String fotoUsuario;

    @Column(name = "is_activo", nullable = false)
    @ApiModelProperty(notes = "Estado del usuario", required = true, example = "true")
    private boolean isActivo;

    @Column(name = "fecha_registro", nullable = false)
    @ApiModelProperty(notes = "Fecha de registro del usuario", required = true, example = "2020-05-25")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    @ApiModelProperty(notes = "Fecha de actualización del usuario", example = "2020-06-01")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    @ApiModelProperty(notes = "Fecha de baja del usuario", example = "2020-06-01")
    private LocalDateTime fechaBaja;

    // USER(1):ROLE(1)
    //FUNGE DE MANYTOONE
    @OneToOne//(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @OneToOne//(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    // USUARIO(1):TOKEN(*)
    //@JsonIgnore
    @OneToMany(mappedBy = "usuario"/*, fetch = FetchType.LAZY, cascade = CascadeType.ALL*/)
    private List<Token> tokens;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmacion() {
        return passwordConfirmacion;
    }

    public void setPasswordConfirmacion(String passwordConfirmacion) {
        this.passwordConfirmacion = passwordConfirmacion;
    }

    public boolean isActivo() {
        return isActivo;
    }

    public void setActivo(boolean activo) {
        isActivo = activo;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
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

    @JsonBackReference
    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Usuario() {
    }

    public Usuario(Long id, String usuario, String password, String passwordConfirmacion, boolean isActivo, String fotoUsuario, LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion, LocalDateTime fechaBaja, Rol rol, Persona persona) {
        this.id = id;
        this.usuario = usuario;
        this.password = password;
        this.passwordConfirmacion = passwordConfirmacion;
        this.isActivo = isActivo;
        this.fotoUsuario = fotoUsuario;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaBaja = fechaBaja;
        this.rol = rol;
        this.persona = persona;
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