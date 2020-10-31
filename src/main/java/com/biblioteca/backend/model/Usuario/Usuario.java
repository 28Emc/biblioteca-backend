package com.biblioteca.backend.model.Usuario;

import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Rol;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(length = 30, nullable = false)
    @ApiModelProperty(notes = "Nombre del usuario", required = true, example = "Pepito")
    private String nombres;

    @Column(name = "apellido_materno", length = 30, nullable = false)
    @ApiModelProperty(notes = "Apellido materno del usuario", required = true, example = "Paredes")
    private String apellidoMaterno;

    @Column(name = "apellido_paterno", length = 30, nullable = false)
    @ApiModelProperty(notes = "Apellido Paterno del usuario", required = true, example = "Rojas")
    private String apellidoPaterno;

    @Column(name = "dni", length = 8, unique = true, nullable = false)
    @ApiModelProperty(notes = "DNI del usuario", required = true, example = "98765432")
    private String dni;

    @Column(length = 200)
    @ApiModelProperty(notes = "Dirección del usuario", example = "Av. Lima 123")
    private String direccion;

    @Column(length = 9, unique = true, nullable = false)
    @ApiModelProperty(notes = "Celular del usuario", required = true, example = "987123654")
    private String celular;

    @Column(name = "email", unique = true, nullable = false)
    @ApiModelProperty(notes = "Email del usuario", required = true, example = "pepe@gmail.com")
    private String email;

    @Column(name = "usuario", length = 30, unique = true, nullable = false)
    @ApiModelProperty(notes = "Alias del usuario", required = true, example = "pepito2020")
    private String usuario;

    @Column(name = "password", nullable = false)
    @ApiModelProperty(notes = "Contraseña del usuario", required = true, example = "$2a$10$mpnvIqpwTF6BJNlr4pXwOOCXk7KZiqZftFHt3IxwZ5ODYMfIBtHg6")
    private String password;

    @Transient
    @Column(name = "confirmar_password")
    @ApiModelProperty(notes = "Confirmar Contraseña del usuario", example = "$2a$10$mpnvIqpwTF6BJNlr4pXwOOCXk7KZiqZftFHt3IxwZ5ODYMfIBtHg6")
    private String passwordConfirmacion;

    @Column(name = "is_activo", nullable = false)
    @ApiModelProperty(notes = "Estado del usuario", required = true, example = "true")
    private boolean isActivo;

    @Column(name = "foto_usuario")
    @ApiModelProperty(notes = "Foto del usuario", example = "pepito2020.png")
    private String fotoUsuario;

    @Column(name = "fecha_registro", nullable = false)
    @ApiModelProperty(notes = "Fecha de creación del usuario", required = true, example = "2020-05-25")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    @ApiModelProperty(notes = "Fecha de actualización del usuario", required = true, example = "2020-06-01")
    private LocalDateTime fechaActualizacion;

    // USER(1):ROLE(1)
    //FUNGE DE MANYTOONE
    @OneToOne//(cascade = CascadeType.MERGE)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "local_id"/*, nullable = false*/)
    private Local local;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @JsonBackReference
    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @JsonBackReference
    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Usuario() {
    }

    public Usuario(Long id, String nombres, String apellidoMaterno, String apellidoPaterno, String dni, String direccion, String celular, String email, String usuario, String password, String passwordConfirmacion, boolean isActivo, String fotoUsuario, LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion, Rol rol, Local local) {
        this.id = id;
        this.nombres = nombres;
        this.apellidoMaterno = apellidoMaterno;
        this.apellidoPaterno = apellidoPaterno;
        this.dni = dni;
        this.direccion = direccion;
        this.celular = celular;
        this.email = email;
        this.usuario = usuario;
        this.password = password;
        this.passwordConfirmacion = passwordConfirmacion;
        this.isActivo = isActivo;
        this.fotoUsuario = fotoUsuario;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
        this.rol = rol;
        this.local = local;
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